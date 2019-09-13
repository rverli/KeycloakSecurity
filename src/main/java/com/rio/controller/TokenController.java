package com.rio.controller;

import java.io.IOException;

import org.json.simple.parser.ParseException;
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

	@Autowired
	private TokenService tokenService;
	
	/**
	 * Get token for the first time when user log in. We need to pass
	 * credentials only once. Later communication will be done by sending token.
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws UnsupportedOperationException 
	 */
	@PostMapping
	@ResponseBody
	public TokenDTO getTokenUsingCredentials( String username, String password ) throws UnsupportedOperationException, ParseException, IOException {
		
		return tokenService.getToken( username, password );
	}

	/**
	 * When access token get expired than send refresh token to get new access
	 * token. We will receive new refresh token also in this response.
	 * @throws IOException 
	 * @throws UnsupportedOperationException 
	 */
	@GetMapping("/refreshtoken")
	@ResponseBody
	public String getTokenUsingRefreshToken( @RequestHeader(value = "Authorization") String refreshToken ) throws UnsupportedOperationException, IOException {

		return tokenService.getByRefreshToken(refreshToken);
	}	
}
