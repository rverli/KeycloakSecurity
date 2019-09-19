package com.rio.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rio.model.TokenDTO;
import com.rio.services.TokenService;

@RestController
@RequestMapping(value = "/v1/token")
public class TokenController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private TokenService tokenService;
	
	/**
	 * Get token for the first time when user log in. We need to pass
	 * credentials only once. Later communication will be done by sending token.
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	@PostMapping
	@ResponseBody
	public TokenDTO getTokenUsingCredentials( String username, String password ) throws Exception {
		
		try {
			return tokenService.getToken( username, password );
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}		
	}

	/**
	 * When access token get expired than send refresh token to get new access
	 * token. We will receive new refresh token also in this response.
	 * @param refreshToken
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	@GetMapping("/refreshtoken")
	@ResponseBody
	public String getTokenUsingRefreshToken( @RequestHeader(value = "Authorization") String refreshToken ) throws Exception {
		
		try {
			return tokenService.getByRefreshToken(refreshToken);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}	
}
