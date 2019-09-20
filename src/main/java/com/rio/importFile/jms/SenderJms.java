package com.rio.importFile.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class SenderJms {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public void send(String destination, String message) {
	    jmsTemplate.convertAndSend(destination, message);
	}
}
