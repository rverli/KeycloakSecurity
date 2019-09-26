package com.rio.controller.impl;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
	
	private static final Logger log = LoggerFactory.getLogger( UserControllerImpl.class );
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/create")
	@ResponseBody
	public UserDTO createUser( @RequestBody @Valid UserDTO userDTO ) throws Exception {
		
		try {			
			return userService.createUserAccount( userDTO, null, null );
		} catch (Exception e) {
			log.error(e.getMessage());			
			throw e;
		}
	}
	
	@GetMapping
	@ResponseBody
	public UserDTO getUser( String username, String email ) throws Exception {		
		
		try {
			return userService.getUserDTO( username, email );
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	@GetMapping("/remove")
	@ResponseBody
	public boolean removeUser( @NotNull String username ) throws Exception {		
		
		try {
			userService.removeUser(username);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	@GetMapping("/removeRoles")
	@ResponseBody
	public boolean removeRoles( @NotNull String username, @NotNull List<String> roles ) throws Exception {		
		
		try {
			userService.removeRoles( null, username, roles, null );
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	@GetMapping("/associateRoles")
	@ResponseBody
	public boolean associateRoles( @NotNull String username, @NotNull List<String> roles ) throws Exception {		
		
		try {
			userService.associateRole( null, username, roles, null, null );
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	@GetMapping("/getRoles")
	@ResponseBody
	public List<RoleDTO> getRolesByUser( @NotNull String username ) throws Exception {
		
		try {
			return userService.getRolesByUser( username, null );
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	@PostMapping("/logout")
	@ResponseBody
	public boolean logoutUser( @NotNull String username ) throws Exception {
		
		try {
			userService.logoutUser( username, null );
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	@PostMapping("/update/password")
	@ResponseBody
	public boolean updatePassword( @NotNull String username, @NotNull String newPassword ) throws Exception {
		
		try {
			userService.resetPassword(newPassword, username, null);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
		return true;
	}
	
	@PostMapping("/update")
	@ResponseBody
	public UserDTO updateUser( @RequestBody @Valid UserDTO userDTO ) throws Exception {

		try {
			return userService.updateUser( userDTO, null );
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}		
}
