package com.rio.importFile.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.sound.midi.Receiver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.rio.controller.UserController;
import com.rio.exceptions.KeycloakException;
import com.rio.exceptions.UsuarioJaCadastradoException;
import com.rio.exceptions.UsuarioNaoEncontradoException;
import com.rio.model.UserDTO;

@Component
public class ListenerJms {

	@Autowired
	private UserController userController;
	
	private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
	
	private static int COUNT = 0; 
	
	@JmsListener(destination = "${destination.queue}")
	public void receive1(String message) {
		
		COUNT+=1;
		logger.info(String.valueOf(COUNT));
		
	  	try {
	  		this.createUser( message );
		} catch (Exception e) {		
			logger.error(e.getMessage());
		}	  
	}
	
	private void createUser( String line ) throws Exception {
		
		try {
			userController.createUser( this.parse( line ) );
		} catch (UsuarioJaCadastradoException e1) {
			logger.error(e1.getMessage());
		} catch (KeycloakException e1) {
			logger.error(e1.getMessage());
		} catch (UsuarioNaoEncontradoException e1) {
			logger.error(e1.getMessage());
		}
	}
	
	private UserDTO parse( String line ) {
		
		StringTokenizer st = new StringTokenizer(line, ";");
		
		UserDTO userDTO = new UserDTO();
		
		userDTO.setUsername( st.nextElement().toString() );
		userDTO.setEmail( st.nextElement().toString() );
		
		String fullName = st.nextElement().toString();
		
		st = new StringTokenizer(fullName, " ");
		
		String firstName = st.nextElement().toString();
		userDTO.setFirstName( firstName.toUpperCase().trim() );
		
		String lastName = "";
		
		while (st.hasMoreElements()) {
			lastName+= " ";
			lastName+= st.nextElement();
		}
		
		lastName = StringUtils.stripStart(lastName, " \t");
		
		userDTO.setLastName( lastName.toUpperCase().trim() );
		
		List<String> roles = new ArrayList<String>();
		roles.add("carioca-rio");
		roles.add("user");
		userDTO.setRoles( roles );
		
		return userDTO;		
	}
}
