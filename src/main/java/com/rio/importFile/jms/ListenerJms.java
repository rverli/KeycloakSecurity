package com.rio.importFile.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.rio.controller.UserController;
import com.rio.exceptions.KeycloakException;
import com.rio.exceptions.UsuarioJaCadastradoException;
import com.rio.exceptions.UsuarioNaoEncontradoException;
import com.rio.model.UserDTO;

@Component
public class ListenerJms {

	private static final Logger logger = LoggerFactory.getLogger( ListenerJms.class );
	
	@Autowired
	private UserController userController;
		
	private static int COUNT = 0; 
	
	@JmsListener(destination = "${destination.queue}")
	public void receive1(String message) {
		
		COUNT+=1;
		logger.info( "" + COUNT );
		
	  	try {
	  		this.createUser( message );
		} catch (Exception e) {		
			logger.error(e.getMessage());
		}	  
	}
	
	private void createUser( String json ) throws Exception {
		
		UserDTO userDTO = new Gson().fromJson(json, UserDTO.class);
		
		try {
			userController.createUser( userDTO );
		} catch (UsuarioJaCadastradoException e1) {
			logger.error(e1.getMessage());
		} catch (KeycloakException e1) {
			logger.error(e1.getMessage());
		} catch (UsuarioNaoEncontradoException e1) {
			logger.error(e1.getMessage());
		}
	}
}
