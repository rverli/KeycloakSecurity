package com.rio.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class TokenDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String accessToken;
	private String expireIn;
	private String refreshToken;
	private String refreshExpiresIn;
	private String tokenType;
}
