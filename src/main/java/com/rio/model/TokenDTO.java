package com.rio.model;

import java.io.Serializable;

public class TokenDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String accessToken;
	private String expireIn;
	private String refreshToken;
	private String refreshExpiresIn;
	private String tokenType;
	
	public TokenDTO() {
		super();
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getExpireIn() {
		return expireIn;
	}

	public void setExpireIn(String expireIn) {
		this.expireIn = expireIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRefreshExpiresIn() {
		return refreshExpiresIn;
	}

	public void setRefreshExpiresIn(String refreshExpiresIn) {
		this.refreshExpiresIn = refreshExpiresIn;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
}
