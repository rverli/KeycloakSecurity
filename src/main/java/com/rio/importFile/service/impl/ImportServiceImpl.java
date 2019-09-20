package com.rio.importFile.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.rio.exceptions.UsuarioNaoEncontradoException;
import com.rio.importFile.jms.SenderJms;
import com.rio.importFile.service.FileStorageService;
import com.rio.importFile.service.ImportService;
import com.rio.model.UserDTO;
import com.rio.services.UserService;
import com.rio.services.impl.UserServiceImpl;

@Service
public class ImportServiceImpl implements ImportService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private SenderJms sender;
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private UserService userService;
	
	@Value("${destination.queue}")
	private String destinationQueue;
	
	@Value("${file.upload-dir}")
	private String UPLOAD_DIR;
	
	public void importFile( MultipartFile file ) throws IOException, UsuarioNaoEncontradoException {
		Set<UserDTO> users = this.extractFileToDTO( file );
		this.verifyList( users );
		this.sendQueue( users );
	}
	
	private Set<UserDTO> extractFileToDTO( MultipartFile file ) throws IOException {
		
		logger.info("Extraindo arquivo!");
		
		String fileName = fileStorageService.storeFile(file);
		
		File csvFile = new File(UPLOAD_DIR + "/" + fileName);
		
		Set<UserDTO> users = new HashSet<UserDTO>();
		
		if ( csvFile.isFile() && csvFile.exists() ) {
			
			String line = null;
			
			try {
				BufferedReader csvReader = new BufferedReader(new FileReader( csvFile ));
				while ((line = csvReader.readLine()) != null) {										
					users.add( this.parse( line ) );
				}
				csvReader.close();
			} catch (IOException e) {
				throw e;
			}
		}
		
		if (csvFile.exists()) {
			csvFile.delete();
		}
		
		return users;
	}
	
	private void verifyList(Set<UserDTO> usersFile) throws UsuarioNaoEncontradoException {
		
		logger.info("Removendo registros que já existem!");
		
		List<UserRepresentation> allUserKeycloak = userService.getUserAll(null, null);
		
		List<UserDTO> usersKeycloak = allUserKeycloak.stream()
				.map( s -> this.parseUserDTO( s ) )
				.collect(Collectors.toList());
		
		usersFile.removeIf( usersKeycloak::contains );		
	}
	
	private void sendQueue( Set<UserDTO> users ) {
		
		logger.info("Enviando para fila!");
		
		for (UserDTO userDTO : users) {
			sender.send( destinationQueue, new Gson().toJson(userDTO) );
		}
	}
	
	private UserDTO parse( String line ) {
		
		StringTokenizer st = new StringTokenizer(line, ";");
		
		UserDTO userDTO = new UserDTO();
		
		try {
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
			
		} catch (Exception e) {
			logger.error(line);
		}
		
		return userDTO;		
	}
	
	private UserDTO parseUserDTO( UserRepresentation userRepresentation ) {
		
		UserDTO user = new UserDTO();
		user.setUsername(userRepresentation.getUsername());
		user.setEmail(userRepresentation.getEmail());
		user.setFirstName(userRepresentation.getFirstName());
		user.setLastName(userRepresentation.getLastName());		
		return user;
	}
}
