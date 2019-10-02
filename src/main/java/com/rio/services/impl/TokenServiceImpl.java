package com.rio.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rio.exceptions.ServiceException;
import com.rio.model.TokenDTO;
import com.rio.services.TokenService;

@Service
public class TokenServiceImpl implements TokenService {

	private static final Logger log = LoggerFactory.getLogger( TokenServiceImpl.class );
	
	@Value("${keycloak.resource}")
	private String CLIENTID;
	
	@Value("${keycloak.credentials.secret}")
	private String SECRETKEY;
	
	@Value("${keycloak.auth-server-url}")
	private String AUTHURL;

	@Value("${keycloak.realm}")
	private String REALM;
	
	public TokenDTO getToken( String username, String password ) 
			throws UnsupportedOperationException, ParseException, IOException, ServiceException {
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("grant_type", "password"));
		urlParameters.add(new BasicNameValuePair("client_id", CLIENTID));
		urlParameters.add(new BasicNameValuePair("username", username));
		urlParameters.add(new BasicNameValuePair("password", password));
		urlParameters.add(new BasicNameValuePair("client_secret", SECRETKEY));
		
		return this.parseToken( this.sendPost( urlParameters ) );
	}

	public TokenDTO getTokenServiceAccount( String clientId, String clientPassword )
			throws UnsupportedOperationException, ParseException, IOException, ServiceException {
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
		urlParameters.add(new BasicNameValuePair("client_id", clientId));		
		urlParameters.add(new BasicNameValuePair("client_secret", clientPassword));
		
		return this.parseToken( this.sendPost( urlParameters ) );
	}
	
	private TokenDTO parseToken( String response ) throws ParseException, ServiceException {
		
		if ( response.contains("error") ) {
			throw new ServiceException(response);
		}
		
		TokenDTO tokenDTO = null;
		try {
			JSONObject json = (JSONObject)new JSONParser().parse( response.toString() );
		
			tokenDTO = new TokenDTO();
			tokenDTO.setAccessToken( json.get("access_token").toString() );
			tokenDTO.setExpireIn( json.get("expires_in").toString() );		
			tokenDTO.setRefreshToken( json.get("refresh_token").toString() );
			tokenDTO.setRefreshExpiresIn( json.get("refresh_expires_in").toString() );
			tokenDTO.setTokenType( json.get("token_type").toString() );
		} catch (ParseException e) {
			log.error(e.getMessage());
			throw e;
		}
		
		return tokenDTO;
	}
	
	public String getByRefreshToken( String refreshToken ) throws UnsupportedOperationException, IOException {

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
		urlParameters.add(new BasicNameValuePair("client_id", CLIENTID));
		urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
		urlParameters.add(new BasicNameValuePair("client_secret", SECRETKEY));

		return this.sendPost( urlParameters );
	}
	
	private String sendPost(List<NameValuePair> urlParameters) throws UnsupportedOperationException, IOException {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/token");

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		return result.toString();
	}
	
}
