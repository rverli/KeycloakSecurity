package com.rio.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rio.exceptions.KeycloakException;
import com.rio.exceptions.UsuarioJaCadastradoException;
import com.rio.exceptions.UsuarioNaoEncontradoException;
import com.rio.model.RoleDTO;
import com.rio.model.UserDTO;
import com.rio.services.UserService;

@Component
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Value("${keycloak.auth-server-url}")
	private String AUTHURL;

	@Value("${keycloak.realm}")
	private String REALM;
	
	@Autowired
	private KeycloakResourcesImpl keycloakResources;

	public UserDTO createUserAccount( UserDTO userDTO, UsersResource usersResource, RealmResource realmResource ) throws UsuarioJaCadastradoException, KeycloakException, UsuarioNaoEncontradoException {

		if ( usersResource == null ) {
			usersResource = keycloakResources.getUsersResourceInstance(AUTHURL, REALM);
		}

		if ( realmResource == null ) {
			realmResource = keycloakResources.getRealmResourceInstance(AUTHURL, REALM);
		}
		
		if ( !existUser( userDTO.getUsername(), realmResource ) ) {

			userDTO = this.createUser( userDTO, usersResource );
			
			this.associateRole( userDTO.getId(), userDTO.getUsername(), userDTO.getRoles(), usersResource, realmResource );
			logger.info("User [" + userDTO.getUsername() + " - " + userDTO.getFirstName() + " " + userDTO.getLastName() + "] created!");
		} else {
			logger.info("User [" + userDTO.getUsername() + " - " + userDTO.getFirstName() + " " + userDTO.getLastName() + "] is already in keycloak database!");
			this.associateRole( userDTO.getId(), userDTO.getUsername(), userDTO.getRoles(), usersResource, realmResource );			
		}
		return userDTO;
	}

	// after logout user from the keycloak system. No new access token will be issued.
	public void logoutUser(String username, UsersResource usersResource) throws UsuarioNaoEncontradoException {

		if ( usersResource == null ) {
			usersResource = keycloakResources.getUsersResourceInstance(AUTHURL, REALM);
		}
		
		String userId = this.getUserId( username, null );
		
		usersResource.get( userId ).logout();
	}

	public void resetPassword(String newPassword, String username, UsersResource userResource) throws UsuarioNaoEncontradoException {

		if ( userResource == null ) {
			userResource = keycloakResources.getUsersResourceInstance(AUTHURL, REALM);			
		}
		
		String userId = this.getUserId( username, null );
		
		// Define password credential
		CredentialRepresentation newCredential = new CredentialRepresentation();
		newCredential.setTemporary(false);
		newCredential.setType(CredentialRepresentation.PASSWORD);
		newCredential.setValue(newPassword.toString().trim());

		// Set password credential
		userResource.get( userId ).resetPassword(newCredential);
		
		logger.info("Password changed! [" + username + "]");
	}

	public UserDTO getUserDTO(String username, String email) throws UsuarioNaoEncontradoException {

		UserResource userResource = this.getUser(username, email, null);

		UserRepresentation representation = userResource.toRepresentation();

		return new UserDTO(
				representation.getId(), 
				representation.getUsername(), 
				representation.getEmail(), 
				representation.getFirstName(),
				representation.getLastName());
	}

	public String getUserId(String username, String email) throws UsuarioNaoEncontradoException {

		UserResource userResource = this.getUser(username, email, null);

		UserRepresentation representation = userResource.toRepresentation();

		return representation.getId();
	}
	
	private UserResource getUser(String username, String email, Keycloak keycloakResource) throws UsuarioNaoEncontradoException {

		if ( keycloakResource == null ) {
			keycloakResource = keycloakResources.getKeycloakResourceInstance(AUTHURL, REALM);			
		}
				
		List<UserRepresentation> retrieveUserList = keycloakResource.realm(REALM).users().search(username, null, null, email, 0, 1);

		if (retrieveUserList.size() != 0) {			
			return keycloakResource.realm(REALM).users().get(retrieveUserList.get(0).getId());
		} else {
			throw new UsuarioNaoEncontradoException(username);
		}
	}
	
	public void removeUser(String username) throws UsuarioNaoEncontradoException {
		
		Keycloak keycloakResource = keycloakResources.getKeycloakResourceInstance(AUTHURL, REALM);
	
		List<UserRepresentation> userList = 
				this.getUserToRemove(username, keycloakResource);
				
		if ( userList != null && userList.size() > 0 ) {
			for (int i = 0; i < userList.size(); i++) {
				keycloakResource.realm( REALM ).users().get(userList.get(i).getId()).remove();
			}
			
			logger.info("Removed " + userList.size() + " users!");
			
			userList = this.getUserToRemove(username, keycloakResource);
			
			if ( userList != null && userList.size() > 0 ) {
				this.removeUser(username);
			}
		}		
	}

	private List<UserRepresentation> getUserToRemove(String username, Keycloak keycloakResource) 
			throws UsuarioNaoEncontradoException {
		
		List<UserRepresentation> userList = new ArrayList<>();
		
		if ( username != null ) {
			
			String userId = this.getUserId(username, null);
			
			UserRepresentation userRepresentation = keycloakResource.realm( REALM ).users().get(userId).toRepresentation();
			userList.add( userRepresentation );
			
		} else {
			userList = keycloakResource.realm(REALM).users().list();			
		}
		
		return userList.size() > 0 ? userList : null;
	}
	
	public UserDTO updateUser(UserDTO userDTO, UserResource userResource) 
			throws UsuarioNaoEncontradoException {

		if ( userResource == null ) {
			userResource = this.getUser(userDTO.getUsername(), null, null);			
		}		

		UserRepresentation user = userResource.toRepresentation();
		user.setEmail(userDTO.getEmail());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());

		userResource.update(user);

		return userDTO;
	}

	private UserRepresentation createUserRepresentation(UserDTO userDTO) {
		
		UserRepresentation user = new UserRepresentation();
		user.setUsername(userDTO.getUsername().toUpperCase());
		user.setEmail(userDTO.getEmail().toLowerCase());
		user.setFirstName(userDTO.getFirstName().toUpperCase());
		user.setLastName(userDTO.getLastName().toUpperCase());
		user.setEnabled(true);		
		return user;
	}
	
	private UserDTO createUser(UserDTO userDTO, UsersResource usersResource) throws UsuarioJaCadastradoException, KeycloakException {

		if ( usersResource == null ) {
			usersResource = keycloakResources.getUsersResourceInstance(AUTHURL, REALM);
		}
		
		int httpResponse = 0;
		String userId;
		
		UserRepresentation user = this.createUserRepresentation( userDTO );

		// Create user
		Response result = usersResource.create(user);
		
		httpResponse = result.getStatus();

		if ( httpResponse == HttpStatus.SC_CREATED ) {

			userId = result.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
			userDTO.setId( userId );

			if ( userDTO.getPassword() != null ) {
				
				//Define password credential
				CredentialRepresentation passwordCred = new CredentialRepresentation();
				passwordCred.setTemporary(false);
				passwordCred.setType(CredentialRepresentation.PASSWORD);
				passwordCred.setValue(userDTO.getPassword());
				
				//Set password credential
				usersResource.get( userId ).resetPassword(passwordCred);
			}

		} else if ( httpResponse == HttpStatus.SC_CONFLICT ) {			
			throw new UsuarioJaCadastradoException(userDTO.getUsername());
		} else {			
			throw new KeycloakException( httpResponse );
		}

		return userDTO;
	}

	private List<RoleRepresentation> getUserRole(String role, RealmResource realmResource) {
		
		if ( realmResource == null ) {
			realmResource = keycloakResources.getRealmResourceInstance(AUTHURL, REALM);
		}
		
		return realmResource.roles().list().stream().filter(r -> r.getName().equals(role))
				.collect(Collectors.toList());
	}

	public boolean existUser( String username, RealmResource realmResource ) {
		
		if ( realmResource == null ) {
			realmResource = keycloakResources.getRealmResourceInstance(AUTHURL, REALM);
		}
		
		List<UserRepresentation> usuarios = realmResource.users().search(username);

		if (!usuarios.isEmpty()) {
			String userId = usuarios.get(0).getId();
			return userId != null && !userId.equals("");
		}
		return false;
	}
	
	public List<RoleDTO> getRolesByUser( String username, UsersResource usersResource ) throws UsuarioNaoEncontradoException {
		
		if ( usersResource == null ) {
			usersResource = keycloakResources.getUsersResourceInstance( AUTHURL, REALM );			
		}
		
		String userId = this.getUserId( username, null );
		
		List<RoleRepresentation> listRoles = usersResource.get( userId ).roles().realmLevel().listAll();
		
		ArrayList<RoleDTO> roles = listRoles.stream()
				.map(RoleDTO::new)
				.collect(Collectors.toCollection(ArrayList::new));
		
		return roles;	  
	}
	
	private boolean existAssociateRole(String userId, String role, UsersResource usersResource) {

		if ( usersResource == null ) {
			usersResource = keycloakResources.getUsersResourceInstance(AUTHURL, REALM);
		}
		
		List<RoleRepresentation> roles = usersResource.get( userId ).roles().realmLevel().listAll();
		
		//this.removeRoles(roles, userId, usersResource);
		
		if (!roles.isEmpty() && roles.size() > 0) {						
			return !roles.stream()
					.filter(r -> r.getName().equals(role))
					.collect(Collectors.toList()).isEmpty();
		}

		return false;
	}

	/*
	 * private void removeRoles( List<RoleRepresentation> roles, String userId,
	 * UsersResource usersResource ) {
	 * 
	 * if ( usersResource == null ) { usersResource =
	 * keycloakResources.getUsersResourceInstance(AUTHURL, REALM); }
	 * 
	 * if ( roles == null || roles.size() <= 0 ) return;
	 * 
	 * List<RoleRepresentation> rolesToRemove = new ArrayList<>();
	 * 
	 * for (RoleRepresentation roleRepresentation : roles) { if (
	 * roleRepresentation.getName().equals("offline_access") ||
	 * roleRepresentation.getName().equals("uma_authorization") ) {
	 * rolesToRemove.add( roleRepresentation ); } }
	 * 
	 * usersResource.get( userId ).roles().realmLevel().remove( rolesToRemove ); }
	 */
	
	private void associateRole(
			String userId, String username, List<String> roles, 
			UsersResource usersResource, RealmResource realmResource) 
					throws UsuarioNaoEncontradoException {

		if ( usersResource == null ) {
			usersResource = keycloakResources.getUsersResourceInstance(AUTHURL, REALM);
		}
		
		if ( realmResource == null ) {
			realmResource = keycloakResources.getRealmResourceInstance(AUTHURL, REALM);
		}
		
		if ( userId == null ) {
			UserResource user = this.getUser(username, null, null);
			userId = user.toRepresentation().getId();
		}
		
		roles = this.removeAdminRole( roles );
		
		List<RoleRepresentation> rolesUser = new ArrayList<>();
		
		if ( roles != null && roles.size() > 0 ) {
			for (String role : roles) {
				
				if ( !this.existAssociateRole( userId, role, usersResource ) ) {
					rolesUser.addAll( this.getUserRole(role, realmResource) );
				}
			}
		}
		
		if (!roles.isEmpty() && rolesUser.size() > 0) {
			usersResource.get( userId ).roles().realmLevel().add( rolesUser );
		}
	}
	
	private List<String> removeAdminRole( List<String> roles ) {
		
		if ( roles == null || roles.size() == 0 ) return null;
		
		if ( roles.contains("admin") ) {
			roles.remove("admin");
		}
		
		return roles;
	}
}
