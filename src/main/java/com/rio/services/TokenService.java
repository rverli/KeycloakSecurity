package com.rio.services;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import com.rio.exceptions.ServiceException;
import com.rio.model.TokenDTO;

public interface TokenService {
	
	TokenDTO getToken( String username, String password ) 
			throws UnsupportedOperationException, ParseException, IOException, ServiceException;
	
	TokenDTO getTokenServiceAccount( String clientId, String clientPassword )
			throws UnsupportedOperationException, ParseException, IOException, ServiceException;
	
	String getByRefreshToken( String refreshToken ) throws UnsupportedOperationException, IOException;
}
