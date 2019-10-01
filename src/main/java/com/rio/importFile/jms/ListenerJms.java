package com.rio.importFile.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.rio.controller.UserController;
import com.rio.model.UserDTO;

@Component
public class ListenerJms {

	private static final Logger log = LoggerFactory.getLogger( ListenerJms.class );
	
	@Autowired
	private UserController userController;
		
	private static int COUNT = 0; 
	
	@JmsListener(destination = "${destination.queue.import}")
	public void receiveImport(String message) {
		
		COUNT+=1;
		log.info( "" + COUNT );
		
		UserDTO userDTO = new Gson().fromJson(message, UserDTO.class);
		
	  	try {
	  		userController.createUser( userDTO, false );
		} catch (Exception e) {		
			log.error(e.getMessage());
		}	  
	}
	
	@JmsListener(destination = "${destination.queue.create}")
	public void receiveCreateUser(String message) {
		
		UserDTO userDTO = new Gson().fromJson(message, UserDTO.class);
		
	  	try {
	  		userController.createUser( userDTO, false );
		} catch (Exception e) {		
			log.error(e.getMessage());
		}	  
	}
}
