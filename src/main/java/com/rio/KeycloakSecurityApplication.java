package com.rio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jms.annotation.EnableJms;

import com.rio.importFile.service.property.FileStorageProperties;

@SpringBootApplication
@EnableJms
@EnableConfigurationProperties({
	FileStorageProperties.class
})
public class KeycloakSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeycloakSecurityApplication.class, args);
	}
}
