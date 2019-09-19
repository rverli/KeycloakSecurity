package com.rio.fileupload.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rio.controller.UserController;
import com.rio.exceptions.KeycloakException;
import com.rio.exceptions.UsuarioJaCadastradoException;
import com.rio.exceptions.UsuarioNaoEncontradoException;
import com.rio.fileupload.jms.SenderJms;
import com.rio.model.UserDTO;

@Service
public class ImportService {

	private static final Logger logger = LoggerFactory.getLogger(ImportService.class);
	
	@Autowired
	private UserController userController;
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private SenderJms sender;
	
	@Value("${destination.queue}")
	private String destinationQueue;
	
	@Value("${file.upload-dir}")
	private String UPLOAD_DIR;
	
	public void importFile( MultipartFile file ) throws Exception {
		
		String fileName = fileStorageService.storeFile(file);
		
		File csvFile = new File(UPLOAD_DIR + "/" + fileName);
		
		if ( csvFile.isFile() && csvFile.exists() ) {
			
			String line = null;
		
			int count = 0;
			
			try {
				BufferedReader csvReader = new BufferedReader(new FileReader( csvFile ));
				while ((line = csvReader.readLine()) != null) {
					count+=1;
					sender.send(destinationQueue, line);
					logger.info(String.valueOf(count));
				}
				csvReader.close();
			} catch (IOException e) {
				throw e;
			}
		}
		
		if (csvFile.exists()) {
			csvFile.delete();
		}
	}
	
	public void createUser( String line ) throws Exception {
		
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
