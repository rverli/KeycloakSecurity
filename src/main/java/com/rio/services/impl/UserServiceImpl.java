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
import com.rio.services.KeycloakResources;
import com.rio.services.UserService;

@Component
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private KeycloakResources keycloakResources;
	
	@Value("${keycloak.realm}")
	private String REALM;
		
	public UserDTO createUserAccount( UserDTO userDTO, UsersResource usersResource, RealmResource realmResource ) throws UsuarioJaCadastradoException, KeycloakException, UsuarioNaoEncontradoException {

		if ( usersResource == null ) {
			usersResource = keycloakResources.getUsersResourceInstance();
		}

		if ( realmResource == null ) {
			realmResource = keycloakResources.getRealmResourceInstance();
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
	
	public void logoutUser(String username, UsersResource usersResource) throws UsuarioNaoEncontradoException {

		if ( usersResource == null ) {
			usersResource = keycloakResources.getUsersResourceInstance();
		}
		
		String userId = this.getUserId( username, null );
		
		usersResource.get( userId ).logout();
		
		logger.info("User " + username + " was logged out!");
	}

	public void resetPassword(String newPassword, String username, UsersResource userResource) throws UsuarioNaoEncontradoException {

		if ( userResource == null ) {
			userResource = keycloakResources.getUsersResourceInstance();			
		}
		
		String userId = this.getUserId( username, null );
		
		CredentialRepresentation newCredential = new CredentialRepresentation();
		newCredential.setTemporary(false);
		newCredential.setType(CredentialRepresentation.PASSWORD);
		newCredential.setValue(newPassword.toString().trim());
		
		userResource.get( userId ).resetPassword(newCredential);
		
		logger.info("Password changed! User [" + username + "]");
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
			keycloakResource = keycloakResources.getKeycloakResourceInstance();			
		}
				
		List<UserRepresentation> retrieveUserList = keycloakResource.realm( REALM ).users().search(username, null, null, email, 0, 1);

		if (retrieveUserList.size() != 0) {			
			return keycloakResource.realm( REALM ).users().get(retrieveUserList.get(0).getId());
		} else {
			throw new UsuarioNaoEncontradoException(username);
		}
	}
	
	public void removeUser(String username) throws UsuarioNaoEncontradoException {
		
		Keycloak keycloakResource = keycloakResources.getKeycloakResourceInstance();
	
		List<UserRepresentation> userList = 
				this.getUserAll(username, keycloakResource);
				
		if ( userList != null && userList.size() > 0 ) {
			for (int i = 0; i < userList.size(); i++) {
				keycloakResource.realm( REALM ).users().get(userList.get(i).getId()).remove();
			}
			
			logger.info("Removed " + userList.size() + " users!");
			
			userList = this.getUserAll(username, keycloakResource);
			
			if ( userList != null && userList.size() > 0 ) {
				this.removeUser(username);
			}
		}		
	}
	
	public List<UserRepresentation> getUserAll( String username, Keycloak keycloakResource ) 
			throws UsuarioNaoEncontradoException {
		
		if ( keycloakResource == null ) {
			keycloakResource = keycloakResources.getKeycloakResourceInstance();			
		}
		
		List<UserRepresentation> userList = new ArrayList<>();
		
		if ( username != null ) {
			
			String userId = this.getUserId(username, null);
			
			UserRepresentation userRepresentation = keycloakResource.realm( REALM ).users().get(userId).toRepresentation();
			userList.add( userRepresentation );
			
		} else {
			userList = keycloakResource.realm(REALM).users().list(1, 100000);			
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
	
	private UserDTO createUser( UserDTO userDTO, UsersResource usersResource ) throws UsuarioJaCadastradoException, KeycloakException {

		if ( usersResource == null ) {
			usersResource = keycloakResources.getUsersResourceInstance();
		}
		
		Response result = usersResource.create( this.parseUserDTOToUserRepresentation( userDTO ) );
		
		int httpResponse = result.getStatus();

		if ( httpResponse == HttpStatus.SC_CREATED ) {
			
			userDTO.setId( result.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1") );
			this.createPassword( userDTO, usersResource );

		} else if ( httpResponse == HttpStatus.SC_CONFLICT ) {			
			throw new UsuarioJaCadastradoException( userDTO.getUsername() );
		} else {			
			throw new KeycloakException( httpResponse );
		}

		return userDTO;
	}

	private void createPassword( UserDTO userDTO, UsersResource usersResource ) {
		
		if ( userDTO.getPassword() != null ) {
			
			CredentialRepresentation pass = new CredentialRepresentation();
			pass.setType( CredentialRepresentation.PASSWORD );
			pass.setValue( userDTO.getPassword() );
			pass.setTemporary( false );
			
			usersResource.get( userDTO.getId() ).resetPassword( pass );
		}
	}
	
	private List<RoleRepresentation> getUserRole( String role, RealmResource realmResource ) {
		
		if ( realmResource == null ) {
			realmResource = keycloakResources.getRealmResourceInstance();
		}
		
		return realmResource.roles()
				.list()
				.stream()
				.filter( r -> r.getName().equals( role ) )
				.collect( Collectors.toList() );
	}

	private boolean existUser( String username, RealmResource realmResource ) {
		
		if ( realmResource == null ) {
			realmResource = keycloakResources.getRealmResourceInstance();
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
			usersResource = keycloakResources.getUsersResourceInstance(  );			
		}
		
		String userId = this.getUserId( username, null );
		
		List<RoleRepresentation> listRoles = usersResource.get( userId ).roles().realmLevel().listAll();
		
		return listRoles.stream()
				 .map( RoleDTO::new )
				 .collect( Collectors.toCollection( ArrayList::new ) );	  
	}
	
	private boolean existAssociateRole( String userId, String role, UsersResource usersResource ) {

		if ( usersResource == null ) {
			usersResource = keycloakResources.getUsersResourceInstance();
		}
		
		List<RoleRepresentation> roles = usersResource.get( userId ).roles().realmLevel().listAll();
		
		if (!roles.isEmpty() && roles.size() > 0) {
			return !roles
					.stream()
					.filter(r -> r.getName().equals(role))
					.collect(Collectors.toList()).isEmpty();
		}

		return false;
	}

	
	public void removeRoles( String userId, String username, List<String> roles, UsersResource usersResource ) 
			throws UsuarioNaoEncontradoException {
	
		if ( usersResource == null ) { 
			usersResource = keycloakResources.getUsersResourceInstance(); 
		}
		
		if ( roles == null || roles.size() <= 0 ) return;
		
		if ( userId == null ) {
			UserResource user = this.getUser(username, null, null);
			userId = user.toRepresentation().getId();
		}
		
		List<RoleRepresentation> rolesToRemove = new ArrayList<>();
		
		RoleRepresentation roleRepresentation = null;
		
		for (String role : roles) { 
						
			roleRepresentation = new RoleRepresentation();
			roleRepresentation.setName( role );
			rolesToRemove.add( roleRepresentation );			 
		}
		
		usersResource.get( userId ).roles().realmLevel().remove( rolesToRemove );		
		logger.info( "Roles [" + roles.toString() + "] removed from user " + username );
	}
	
	
	public void associateRole( String userId, String username, List<String> roles, 
			UsersResource usersResource, RealmResource realmResource) 
					throws UsuarioNaoEncontradoException {

		if ( usersResource == null ) {
			usersResource = keycloakResources.getUsersResourceInstance();
		}
		
		if ( realmResource == null ) {
			realmResource = keycloakResources.getRealmResourceInstance();
		}
		
		if ( userId == null ) {
			UserResource user = this.getUser(username, null, null);
			userId = user.toRepresentation().getId();
		}
		
		roles.removeIf( "admin"::contains );
		
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
			logger.info("Associated role(s) " + rolesUser.toString() + " to user [" + username + "]");
		}
	}
	
	private UserRepresentation parseUserDTOToUserRepresentation(UserDTO userDTO) {
		
		UserRepresentation user = new UserRepresentation();
		user.setUsername(userDTO.getUsername().toUpperCase());
		user.setEmail(userDTO.getEmail().toLowerCase());
		user.setFirstName(userDTO.getFirstName().toUpperCase());
		user.setLastName(userDTO.getLastName().toUpperCase());
		user.setEnabled(true);		
		return user;
	}
}
