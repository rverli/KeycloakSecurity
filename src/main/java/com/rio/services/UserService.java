package com.rio.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rio.exceptions.UsuarioJaCadastradoException;
import com.rio.model.UserDTO;

@Component
public class UserService {

	@Autowired
	private KeycloakResources keycloakResources;
	
	private RealmResource realmResource;
	private UsersResource usersResource;
	
	private String userId;
	
	private static String USER_ROLE = "user";
		
	public UserDTO createUserAccount( UserDTO userDTO ) throws Exception {
		
		usersResource = keycloakResources.getKeycloakUserResource();
		realmResource = keycloakResources.getRealmResource();
		
		if(!existeUsuario( userDTO.getUsername() )) {

			userDTO = this.createUserInKeyCloak( userDTO );
			this.associaRoleUserAContaDoUsuario( userId );
	
		} else {
			
			if(!existeRoleUserAssociadaAContaDoUsuario(userId)) {
				this.associaRoleUserAContaDoUsuario(userId);
			}
		}
		return userDTO;
	}
	
	// after logout user from the keycloak system. No new access token will be issued.
	public void logoutUser( String userId ) {

		UsersResource userRessource = keycloakResources.getKeycloakUserResource();
		userRessource.get( userId ).logout();
	}
	
	public void resetPassword(String newPassword, String userId) {

		UsersResource userResource = keycloakResources.getKeycloakUserResource();

		// Define password credential
		CredentialRepresentation newCredential = new CredentialRepresentation();
		newCredential.setTemporary(false);
		newCredential.setType(CredentialRepresentation.PASSWORD);
		newCredential.setValue( newPassword.toString().trim() );

		// Set password credential
		userResource.get( userId ).resetPassword( newCredential );
	}

	private UserDTO createUserInKeyCloak( UserDTO userDTO ) throws Exception {

		int statusId = 0;
		
		try {
			UserRepresentation user = new UserRepresentation();
			user.setUsername(userDTO.getUsername());
			user.setEmail(userDTO.getEmail());
			user.setFirstName(userDTO.getFirstName());
			user.setLastName(userDTO.getLastName());			
			user.setEnabled(true);
			
			// Create user
			Response result = usersResource.create( user );
			System.out.println("Keycloak create user response code>>>>" + result.getStatus());

			statusId = result.getStatus();

			if ( statusId == HttpStatus.SC_CREATED ) {

				userId = result.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
				userDTO.setUserId( userId );
				
				System.out.println("User created with userId:" + userId);

				// Define password credential
				CredentialRepresentation passwordCred = new CredentialRepresentation();
				passwordCred.setTemporary(false);
				passwordCred.setType(CredentialRepresentation.PASSWORD);
				passwordCred.setValue(userDTO.getPassword());

				// Set password credential
				usersResource.get( userId ).resetPassword(passwordCred);
				
				System.out.println("Username==" + userDTO.getUsername() + " created in keycloak successfully");

			} else if ( statusId == HttpStatus.SC_CONFLICT ) {
				System.out.println("Username==" + userDTO.getUsername() + " already present in keycloak");
				throw new UsuarioJaCadastradoException( userDTO.getUsername() );
			} else {
				System.out.println("Username==" + userDTO.getUsername() + " could not be created in keycloak");
			}

		} catch (Exception e) {
			throw e;
		}

		return userDTO;
	}

	private List<RoleRepresentation> getUserRole() {
		
		return realmResource.roles().list().stream()
				.filter(r -> r.getName().equals( USER_ROLE ))
				.collect(Collectors.toList());
	}
	
	private boolean existeRoleUserAssociadaAContaDoUsuario(String userId) {
		
		List<RoleRepresentation> roles = usersResource.get( userId ).roles().realmLevel().listAll();
		
		if(!roles.isEmpty()) {			
			return !roles.stream()
					.filter(r -> r.getName().equals( USER_ROLE ))
					.collect(Collectors.toList()).isEmpty();			
		}
		
		return false;
	}
	
	private void associaRoleUserAContaDoUsuario( String userId ) {
		
		List<RoleRepresentation> roles = this.getUserRole();
		
		if(!roles.isEmpty()) {
			usersResource.get(userId).roles().realmLevel().add(roles);
		}
	}
	
	private boolean existeUsuario(String username) {
		
		List<UserRepresentation> usuarios = realmResource.users().search( username );
		
		if(!usuarios.isEmpty()) {		
			userId = usuarios.get(0).getId();
			return userId != null && !userId.equals("");
		}
		return false;
	}
}
