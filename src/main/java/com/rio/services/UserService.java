package com.rio.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rio.exceptions.KeycloakException;
import com.rio.exceptions.UsuarioJaCadastradoException;
import com.rio.exceptions.UsuarioNaoEncontradoException;
import com.rio.model.RoleDTO;
import com.rio.model.UserDTO;

@Component
public class UserService {

	@Value("${keycloak.auth-server-url}")
	private String AUTHURL;

	@Value("${keycloak.realm}")
	private String REALM;

	public UserDTO createUserAccount(UserDTO userDTO) throws UsuarioJaCadastradoException, KeycloakException, UsuarioNaoEncontradoException {

		if ( !existeUsuario( userDTO.getUsername() ) ) {

			userDTO = this.createUser( userDTO );
						
			this.associarRole( userDTO.getUsername(), userDTO.getRoles() );			
		} else {
			this.associarRole( userDTO.getUsername(), userDTO.getRoles() );			
		}
		return userDTO;
	}

	// after logout user from the keycloak system. No new access token will be
	// issued.
	public void logoutUser(String username) throws UsuarioNaoEncontradoException {

		UsersResource userRessource = KeycloakResources.getUsersResourceInstance(AUTHURL, REALM);
		
		UserDTO user = this.getUserDTO( username );
		
		userRessource.get( user.getId() ).logout();
	}

	public void resetPassword(String newPassword, String username) throws UsuarioNaoEncontradoException {

		UsersResource userResource = KeycloakResources.getUsersResourceInstance(AUTHURL, REALM);
		
		UserDTO user = this.getUserDTO( username );
		
		// Define password credential
		CredentialRepresentation newCredential = new CredentialRepresentation();
		newCredential.setTemporary(false);
		newCredential.setType(CredentialRepresentation.PASSWORD);
		newCredential.setValue(newPassword.toString().trim());

		// Set password credential
		userResource.get( user.getId() ).resetPassword(newCredential);
	}

	public UserDTO getUserDTO(String username) throws UsuarioNaoEncontradoException {

		UserResource userResource = this.getUser(username);

		UserRepresentation representation = userResource.toRepresentation();

		return new UserDTO(
				representation.getId(), 
				representation.getUsername(), 
				representation.getEmail(), 
				representation.getFirstName(),
				representation.getLastName());
	}

	private UserResource getUser(String username) throws UsuarioNaoEncontradoException {

		List<UserRepresentation> retrieveUserList = KeycloakResources.getKeycloakResourceInstance(AUTHURL, REALM)
				.realm(REALM).users().search(username, null, null, null, 0, 1);

		if (retrieveUserList.size() != 0) {

			UserResource retrievedUser = KeycloakResources.getKeycloakResourceInstance(AUTHURL, REALM).realm(REALM)
					.users().get(retrieveUserList.get(0).getId());
			return retrievedUser;
		} else {
			throw new UsuarioNaoEncontradoException(username);
		}
	}

	public UserDTO updateUser(UserDTO userDTO) throws UsuarioNaoEncontradoException {

		UserResource userResource = this.getUser(userDTO.getUsername());

		UserRepresentation user = userResource.toRepresentation();
		user.setEmail(userDTO.getEmail());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());

		userResource.update(user);

		return userDTO;
	}

	private UserDTO createUser(UserDTO userDTO) throws UsuarioJaCadastradoException, KeycloakException {

		int httpResponse = 0;
		String userId;

		UsersResource usersResource = KeycloakResources.getUsersResourceInstance(AUTHURL, REALM);
		
		UserRepresentation user = new UserRepresentation();
		user.setUsername(userDTO.getUsername());
		user.setEmail(userDTO.getEmail());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEnabled(true);

		// Create user
		Response result = usersResource.create(user);
		
		httpResponse = result.getStatus();

		if ( httpResponse == HttpStatus.SC_CREATED ) {

			userId = result.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
			userDTO.setId( userId );

			// Define password credential
			CredentialRepresentation passwordCred = new CredentialRepresentation();
			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);
			passwordCred.setValue(userDTO.getPassword());

			// Set password credential
			usersResource.get( userId ).resetPassword(passwordCred);

		} else if ( httpResponse == HttpStatus.SC_CONFLICT ) {			
			throw new UsuarioJaCadastradoException(userDTO.getUsername());
		} else {			
			throw new KeycloakException( httpResponse );
		}

		return userDTO;
	}

	private List<RoleRepresentation> getUserRole(String role) {

		RealmResource realmResourceInstance = KeycloakResources.getRealmResourceInstance(AUTHURL, REALM);

		return realmResourceInstance.roles().list().stream().filter(r -> r.getName().equals(role))
				.collect(Collectors.toList());
	}

	public boolean existeUsuario( String username ) {

		RealmResource realmResourceInstance = KeycloakResources.getRealmResourceInstance(AUTHURL, REALM);
		
		List<UserRepresentation> usuarios = realmResourceInstance.users().search(username);

		if (!usuarios.isEmpty()) {
			String userId = usuarios.get(0).getId();
			return userId != null && !userId.equals("");
		}
		return false;
	}
	
	public List<RoleDTO> getRolesByUser( String username ) throws UsuarioNaoEncontradoException {
		
		UsersResource usersResource = KeycloakResources.getUsersResourceInstance( AUTHURL, REALM );
		
		UserDTO user = this.getUserDTO( username );
		
		List<RoleRepresentation> listRoles = usersResource.get( user.getId() ).roles().realmLevel().listAll();
		
		ArrayList<RoleDTO> roles = listRoles.stream()
				.map(RoleDTO::new)
				.collect(Collectors.toCollection(ArrayList::new));
		
		return roles;	  
	}
	
	private boolean existeRoleAssociada(String userId, String role) {

		UsersResource usersResource = KeycloakResources.getUsersResourceInstance(AUTHURL, REALM);

		List<RoleRepresentation> roles = usersResource.get( userId ).roles().realmLevel().listAll();

		if (!roles.isEmpty()) {
			return !roles.stream().filter(r -> r.getName().equals(role)).collect(Collectors.toList()).isEmpty();
		}

		return false;
	}
	
	private void associarRole(String username, List<String> roles) throws UsuarioNaoEncontradoException {

		UsersResource usersResource = KeycloakResources.getUsersResourceInstance(AUTHURL, REALM);

		UserDTO user = this.getUserDTO( username );
		
		List<RoleRepresentation> rolesUser = new ArrayList<>();
		
		if ( roles != null && roles.size() > 0 ) {
			for (String role : roles) {
				
				if ( !this.existeRoleAssociada( user.getId(), role) ) {
					rolesUser.addAll( this.getUserRole(role) );
				}
			}
		}

		if (!roles.isEmpty()) {
			usersResource.get( user.getId() ).roles().realmLevel().add( rolesUser );
		}
	}
}
