package com.rio.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rio.model.RoleDTO;
import com.rio.model.UserDTO;
import com.rio.services.UserService;

@RestController
@RequestMapping(value = "/v1/user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	UserService userService;
	
	/**
	 * Creating user in keycloak passing UserDTO contains username, email,
	 * password, firtname, lastname, roles
	 * @param userDTO
	 * @return
	 * @throws Exception 
	 */	
	@PostMapping("/create")
	@ResponseBody
	public UserDTO createUser( @RequestBody UserDTO userDTO ) throws Exception {
		
		try {
			return userService.createUserAccount( userDTO, null, null );
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Get user 
	 * @param username
	 * @return
	 * @throws Exception 
	 */
	@GetMapping
	@ResponseBody
	public UserDTO getUser( String username, String email ) throws Exception {		
		
		try {
			return userService.getUserDTO( username, email );
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Remove user 
	 * @param username
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/removeAll")
	@ResponseBody
	public void removeAllUsers( String username, String email ) throws Exception {		
		
		try {
			userService.removeAllUsers();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Verify if user was already created by another system or person
	 * @param username
	 * @return
	 */
	@GetMapping("/existUser")
	@ResponseBody
	public boolean existUser( String username ) throws Exception {
		
		try {
			return userService.existeUsuario( username, null );
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Get all user's roles
	 * @param username
	 * @return
	 * @throws Exception 
	 */
	@GetMapping("/getRoles")
	@ResponseBody
	public List<RoleDTO> getRolesByUser( String username ) throws Exception {
		
		try {
			return userService.getRolesByUser( username, null );
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Logout the user on keycloak
	 * @param userId
	 * @throws Exception 
	 */
	@PostMapping("/logout")
	@ResponseBody
	public void logoutUser(String username) throws Exception {
		
		try {
			userService.logoutUser( username, null );
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Update user's password
	 * @param username
	 * @param newPassword
	 * @throws Exception 
	 */
	@PostMapping("/update/password")
	@ResponseBody
	public void updatePassword(String username, String newPassword) throws Exception {
		
		try {
			userService.resetPassword(newPassword, username, null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Update user's fields
	 * @param userDTO
	 * @return
	 * @throws Exception 
	 */
	@PostMapping("/update")
	@ResponseBody
	public UserDTO updateUser( @RequestBody UserDTO userDTO ) throws Exception {

		try {
			return userService.updateUser( userDTO, null );
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}		
}
