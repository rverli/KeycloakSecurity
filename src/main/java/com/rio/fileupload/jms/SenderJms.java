package com.rio.fileupload.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class SenderJms {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SenderJms.class);
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public void send(String destination, String message) {
		
	    jmsTemplate.convertAndSend(destination, message);
	    LOGGER.info("sending message='{}' to destination='{}'", message, destination);
	  }
}
