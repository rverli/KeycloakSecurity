package com.rio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rio.model.UserDTO;
import com.rio.services.TokenService;
import com.rio.services.UserService;

@RestController
@RequestMapping(value = "/v1/user")
public class KeycloakController {

	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;
	
	/*
	 * Creating user in keycloak passing UserDTO contains username, emailid,
	 * password, firtname, lastname
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)	
	public ResponseEntity<?> createUser( @RequestBody UserDTO userDTO ) {
		
		try {
			userDTO = userService.createUserAccount( userDTO );
			
			return new ResponseEntity<>(userDTO, HttpStatus.OK);
			
		} catch (Exception e) {			
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	/*
	 * Get token for the first time when user log in. We need to pass
	 * credentials only once. Later communication will be done by sending token.
	 */
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public ResponseEntity<?> getTokenUsingCredentials( @RequestBody UserDTO userDTO ) {

		String responseToken = null;
		
		try {
			responseToken = tokenService.getToken( userDTO );
			System.out.println("Response token = " + responseToken);
			
		} catch (Exception e) {
			System.out.println("Error = " + e.getCause());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(responseToken, HttpStatus.OK);
	}

	/*
	 * When access token get expired than send refresh token to get new access
	 * token. We will receive new refresh token also in this response.Update
	 * client cookie with updated refresh and access token
	 */
	@RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
	public ResponseEntity<?> getTokenUsingRefreshToken( @RequestHeader(value = "Authorization") String refreshToken ) {

		String responseToken = null;
		try {
			responseToken = tokenService.getByRefreshToken(refreshToken);

		} catch (Exception e) {			
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(responseToken, HttpStatus.OK);
	}	
}
