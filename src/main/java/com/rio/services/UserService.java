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

	private String userId;

	public UserDTO createUserAccount(UserDTO userDTO) throws UsuarioJaCadastradoException {

		if (!existeUsuario(userDTO.getUsername())) {

			userDTO = this.createUser(userDTO);
			this.associaRoleUserAContaDoUsuario(userId, userDTO.getRole());

		} else {

			if (!existeRoleUserAssociadaAContaDoUsuario(userId, userDTO.getRole())) {
				this.associaRoleUserAContaDoUsuario(userId, userDTO.getRole());
			}
		}
		return userDTO;
	}

	// after logout user from the keycloak system. No new access token will be
	// issued.
	public void logoutUser(String userId) {

		UsersResource userRessource = KeycloakResources.getUsersResourceInstance(AUTHURL, REALM);
		userRessource.get(userId).logout();
	}

	public void resetPassword(String newPassword, String userId) {

		UsersResource userResource = KeycloakResources.getUsersResourceInstance(AUTHURL, REALM);

		// Define password credential
		CredentialRepresentation newCredential = new CredentialRepresentation();
		newCredential.setTemporary(false);
		newCredential.setType(CredentialRepresentation.PASSWORD);
		newCredential.setValue(newPassword.toString().trim());

		// Set password credential
		userResource.get(userId).resetPassword(newCredential);
	}

	public UserDTO getUserDTO(String username) throws UsuarioNaoEncontradoException {

		UserResource userResource = this.getUser(username);

		UserRepresentation representation = userResource.toRepresentation();

		return new UserDTO(representation.getUsername(), representation.getEmail(), representation.getFirstName(),
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

	private UserDTO createUser(UserDTO userDTO) throws UsuarioJaCadastradoException {

		int statusId = 0;

		UsersResource usersResource = KeycloakResources.getUsersResourceInstance(AUTHURL, REALM);
		
		UserRepresentation user = new UserRepresentation();
		user.setUsername(userDTO.getUsername());
		user.setEmail(userDTO.getEmail());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEnabled(true);

		// Create user
		Response result = usersResource.create(user);
		System.out.println("Keycloak create user response code>>>>" + result.getStatus());

		statusId = result.getStatus();

		if (statusId == HttpStatus.SC_CREATED) {

			userId = result.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
			userDTO.setUserId(userId);

			System.out.println("User created with userId:" + userId);

			// Define password credential
			CredentialRepresentation passwordCred = new CredentialRepresentation();
			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);
			passwordCred.setValue(userDTO.getPassword());

			// Set password credential
			usersResource.get(userId).resetPassword(passwordCred);

			System.out.println("Username==" + userDTO.getUsername() + " created in keycloak successfully");

		} else if (statusId == HttpStatus.SC_CONFLICT) {
			System.out.println("Username==" + userDTO.getUsername() + " already present in keycloak");
			throw new UsuarioJaCadastradoException(userDTO.getUsername());
		} else {
			System.out.println("Username==" + userDTO.getUsername() + " could not be created in keycloak");
		}

		return userDTO;
	}

	private List<RoleRepresentation> getUserRole(String role) {

		RealmResource realmResourceInstance = KeycloakResources.getRealmResourceInstance(AUTHURL, REALM);

		return realmResourceInstance.roles().list().stream().filter(r -> r.getName().equals(role))
				.collect(Collectors.toList());
	}

	private boolean existeRoleUserAssociadaAContaDoUsuario(String userId, String role) {

		UsersResource usersResource = KeycloakResources.getUsersResourceInstance(AUTHURL, REALM);

		List<RoleRepresentation> roles = usersResource.get(userId).roles().realmLevel().listAll();

		if (!roles.isEmpty()) {
			return !roles.stream().filter(r -> r.getName().equals(role)).collect(Collectors.toList()).isEmpty();
		}

		return false;
	}
	
	public List<RoleDTO> getRolesByUser( String userId ) {
	
		UsersResource usersResource = KeycloakResources.getUsersResourceInstance( AUTHURL, REALM );
		
		List<RoleRepresentation> listRoles = usersResource.get( userId ).roles().realmLevel().listAll();
		
		ArrayList<RoleDTO> roles = listRoles.stream()
				.map(RoleDTO::new)
				.collect(Collectors.toCollection(ArrayList::new));
		
		return roles;	  
	}

	private void associaRoleUserAContaDoUsuario(String userId, String role) {

		UsersResource usersResource = KeycloakResources.getUsersResourceInstance(AUTHURL, REALM);

		List<RoleRepresentation> roles = this.getUserRole(role);

		if (!roles.isEmpty()) {
			usersResource.get(userId).roles().realmLevel().add(roles);
		}
	}

	public boolean existeUsuario( String username ) {

		RealmResource realmResourceInstance = KeycloakResources.getRealmResourceInstance(AUTHURL, REALM);

		List<UserRepresentation> usuarios = realmResourceInstance.users().search(username);

		if (!usuarios.isEmpty()) {
			userId = usuarios.get(0).getId();
			return userId != null && !userId.equals("");
		}
		return false;
	}
}
