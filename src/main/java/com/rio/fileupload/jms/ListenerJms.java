package com.rio.fileupload.jms;

import javax.sound.midi.Receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.rio.fileupload.service.ImportService;

@Component
public class ListenerJms {

	private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
	
	@Autowired
	private ImportService importService;
	
	@JmsListener(destination = "${destination.queue}")
	public void receive(String message) {		
	  	try {
	  		importService.createUser( message );
		} catch (Exception e) {		
			logger.error(e.getMessage());
		}	  
	}
}
