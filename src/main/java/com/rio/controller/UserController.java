package com.rio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rio.exceptions.UsuarioJaCadastradoException;
import com.rio.exceptions.UsuarioNaoEncontradoException;
import com.rio.model.RoleDTO;
import com.rio.model.UserDTO;
import com.rio.services.UserService;

@RestController
@RequestMapping(value = "/v1/user")
public class UserController {

	@Autowired
	UserService userService;

	/**
	 * Creating user in keycloak passing UserDTO contains username, emailid,
	 * password, firtname, lastname
	 * @throws UsuarioJaCadastradoException 
	 * @throws Exception 
	 */
	@PostMapping("/create")
	@ResponseBody
	public UserDTO createUser( @RequestBody UserDTO userDTO ) throws UsuarioJaCadastradoException {
		return userService.createUserAccount( userDTO );
	}
	
	@GetMapping("/{username}")
	@ResponseBody
	public UserDTO getUser( @PathVariable String username ) throws UsuarioNaoEncontradoException {
		return userService.getUserDTO( username );
	}
	
	@GetMapping("/existUser/{username}")
	@ResponseBody
	public boolean existUser( @PathVariable String username ) throws UsuarioNaoEncontradoException {
		return userService.existeUsuario( username );
	}
	
	@GetMapping("/getRoles/{userId}")
	@ResponseBody
	public List<RoleDTO> getRolesByUser( @PathVariable String userId ) {
		return userService.getRolesByUser( userId );
	}
	
	@PostMapping("/logout")
	@ResponseBody
	public void logoutUser(String userId) {
		userService.logoutUser(userId);
	}

	@PostMapping("/update/password")
	@ResponseBody
	public void updatePassword1(String userId, String newPassword) {
		userService.resetPassword(newPassword, userId);
	}
	
	@PostMapping("/update")
	@ResponseBody
	public UserDTO updateUser( @RequestBody UserDTO userDTO ) throws UsuarioNaoEncontradoException {
		return userService.updateUser( userDTO );
	}		
}
