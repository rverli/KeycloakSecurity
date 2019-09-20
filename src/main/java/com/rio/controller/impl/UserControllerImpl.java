package com.rio.controller.impl;

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

import com.rio.controller.UserController;
import com.rio.model.RoleDTO;
import com.rio.model.UserDTO;
import com.rio.services.UserService;

@RestController
@RequestMapping(value = "/v1/user")
public class UserControllerImpl implements UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserControllerImpl.class);
	
	@Autowired
	private UserService userService;
		
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
	
	@GetMapping("/remove")
	@ResponseBody
	public void removeUser( String username ) throws Exception {		
		
		try {
			userService.removeUser(username);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	@GetMapping("/existUser")
	@ResponseBody
	public boolean existUser( String username ) throws Exception {
		
		try {
			return userService.existUser( username, null );
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
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
