package com.rio.services;

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;

import com.rio.exceptions.KeycloakException;
import com.rio.exceptions.UsuarioJaCadastradoException;
import com.rio.exceptions.UsuarioNaoEncontradoException;
import com.rio.model.RoleDTO;
import com.rio.model.UserDTO;

public interface UserService {

	UserDTO createUserAccount( UserDTO userDTO, UsersResource usersResource, RealmResource realmResource ) 
			throws UsuarioJaCadastradoException, KeycloakException, UsuarioNaoEncontradoException;
	
	void logoutUser(String username, UsersResource usersResource) throws UsuarioNaoEncontradoException;

	void resetPassword(String newPassword, String username, UsersResource userResource) throws UsuarioNaoEncontradoException;

	UserDTO getUserDTO(String username, String email) throws UsuarioNaoEncontradoException;

	String getUserId(String username, String email) throws UsuarioNaoEncontradoException;
	
	void removeUser(String username) throws UsuarioNaoEncontradoException;
	
	UserDTO updateUser(UserDTO userDTO, UserResource userResource) throws UsuarioNaoEncontradoException;
	
	boolean existUser( String username, RealmResource realmResource );
	
	List<RoleDTO> getRolesByUser( String username, UsersResource usersResource ) throws UsuarioNaoEncontradoException;
	
	List<UserRepresentation> getUserAll( String username, Keycloak keycloakResource ) throws UsuarioNaoEncontradoException;
}
