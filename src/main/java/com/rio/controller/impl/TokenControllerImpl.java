package com.rio.controller.impl;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rio.controller.TokenController;
import com.rio.model.TokenDTO;
import com.rio.services.TokenService;

@RestController
@RequestMapping(value = "/v1/token")
public class TokenControllerImpl implements TokenController {

	private static final Logger log = LoggerFactory.getLogger( TokenControllerImpl.class );
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping
	@ResponseBody
	public TokenDTO getTokenUsingCredentials( @NotNull String username, @NotNull String password ) throws Exception {
		
		try {
			return tokenService.getToken( username, password );
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}		
	}
	
	@GetMapping("/refreshtoken")
	@ResponseBody
	public String getTokenUsingRefreshToken( @RequestHeader(value = "Authorization") String refreshToken ) throws Exception {
		
		try {
			return tokenService.getByRefreshToken(refreshToken);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}	
}
