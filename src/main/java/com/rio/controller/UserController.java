package com.rio.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.rio.model.RoleDTO;
import com.rio.model.UserDTO;

public interface UserController {
	
	/**
	 * Creating user in keycloak passing UserDTO contains username, email,
	 * password, firtname, lastname, roles
	 * @param userDTO
	 * @return
	 * @throws Exception 
	 */	
	UserDTO createUser( @RequestBody UserDTO userDTO ) throws Exception;
	
	/**
	 * Get user 
	 * @param username
	 * @return
	 * @throws Exception 
	 */	
	UserDTO getUser( String username, String email ) throws Exception;
	
	/**
	 * Remove user 
	 * @param username
	 * @return
	 * @throws Exception 
	 */
	boolean removeUser( String username ) throws Exception;
	
	/**
	 * Get all user's roles
	 * @param username
	 * @return
	 * @throws Exception 
	 */	
	List<RoleDTO> getRolesByUser( String username ) throws Exception;
	
	/**
	 * Logout the user on keycloak
	 * @param userId
	 * @throws Exception 
	 */
	boolean logoutUser(String username) throws Exception;

	/**
	 * Update user's password
	 * @param username
	 * @param newPassword
	 * @throws Exception 
	 */	
	boolean updatePassword(String username, String newPassword) throws Exception;
	
	/**
	 * Update user's fields
	 * @param userDTO
	 * @return
	 * @throws Exception 
	 */
	UserDTO updateUser( @RequestBody UserDTO userDTO ) throws Exception;		
}
