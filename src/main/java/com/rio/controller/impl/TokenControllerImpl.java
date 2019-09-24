package com.rio.controller.impl;

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

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/v1/token")
@Slf4j
public class TokenControllerImpl implements TokenController {
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping
	@ResponseBody
	public TokenDTO getTokenUsingCredentials( String username, String password ) throws Exception {
		
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
