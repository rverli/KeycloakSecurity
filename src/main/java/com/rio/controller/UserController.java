package com.rio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rio.exceptions.KeycloakException;
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
	 * @throws UsuarioNaoEncontradoException 
	 * @throws KeycloakException 
	 * @throws Exception 
	 */
	@PostMapping("/create")
	@ResponseBody
	public UserDTO createUser( @RequestBody UserDTO userDTO ) 
			throws UsuarioJaCadastradoException, KeycloakException, UsuarioNaoEncontradoException {
		return userService.createUserAccount( userDTO );
	}
	
	@GetMapping
	@ResponseBody
	public UserDTO getUser( String username ) throws UsuarioNaoEncontradoException {
		return userService.getUserDTO( username );
	}
	
	@GetMapping("/existUser")
	@ResponseBody
	public boolean existUser( String username ) throws UsuarioNaoEncontradoException {
		return userService.existeUsuario( username );
	}
	
	@GetMapping("/getRoles")
	@ResponseBody
	public List<RoleDTO> getRolesByUser( String username ) throws UsuarioNaoEncontradoException {
		return userService.getRolesByUser( username );
	}
	
	@PostMapping("/logout")
	@ResponseBody
	public void logoutUser(String userId) throws UsuarioNaoEncontradoException {
		userService.logoutUser(userId);
	}

	@PostMapping("/update/password")
	@ResponseBody
	public void updatePassword(String username, String newPassword) throws UsuarioNaoEncontradoException {
		userService.resetPassword(newPassword, username);
	}
	
	@PostMapping("/update")
	@ResponseBody
	public UserDTO updateUser( @RequestBody UserDTO userDTO ) throws UsuarioNaoEncontradoException {
		return userService.updateUser( userDTO );
	}		
}
