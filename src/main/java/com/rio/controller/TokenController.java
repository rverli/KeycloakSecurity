package com.rio.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.RequestHeader;

import com.rio.model.TokenDTO;

public interface TokenController {
	
	/**
	 * Get token for the first time when user log in. We need to pass
	 * credentials only once. Later communication will be done by sending token.
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	public TokenDTO getTokenUsingCredentials( String username, String password ) throws Exception;

	/**
	 * When access token get expired than send refresh token to get new access
	 * token. We will receive new refresh token also in this response.
	 * @param refreshToken
	 * @return
	 * @throws Exception 
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */	
	public String getTokenUsingRefreshToken( @RequestHeader(value = "Authorization") String refreshToken ) throws Exception;
}
