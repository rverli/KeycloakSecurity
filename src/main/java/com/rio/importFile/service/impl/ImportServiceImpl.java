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

@Service
public class ImportServiceImpl implements ImportService {

	private static final Logger log = LoggerFactory.getLogger( ImportServiceImpl.class );
	
	@Autowired
	private SenderJms sender;
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private UserService userService;
	
	@Value("${destination.queue.import}")
	private String destinationQueue;
	
	@Value("${file.upload-dir}")
	private String UPLOAD_DIR;
	
	public void importFile( MultipartFile file ) throws IOException, UsuarioNaoEncontradoException {
		
		Set<UserDTO> users = this.fileToDTO( file );
		
		users = this.verifyList( users );

		this.sendQueue( users );
	}
	
	/**
	 * Extract users from file and convert it to DTO list
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private Set<UserDTO> fileToDTO( MultipartFile file ) throws IOException {
		
		log.info("Extrating file to UserDTO list!");
		
		String fileName = fileStorageService.storeFile(file);
		
		File csvFile = new File( UPLOAD_DIR + "/" + fileName );
		
		Set<UserDTO> users = new HashSet<UserDTO>();
		
		if ( csvFile.isFile() && csvFile.exists() ) {
			
			String line = null;
			
			BufferedReader csvReader = null;
			
			try {
				csvReader = new BufferedReader(new FileReader( csvFile ));
				while ((line = csvReader.readLine()) != null) {										
					users.add( this.parse( line ) );
				}
			} catch (IOException e) {
				throw e;
			} finally {
				csvReader.close();				
			}
		}
		
		if ( csvFile.exists() ) {
			csvFile.delete();
		}
		
		return users;
	}
	
	/**
	 * Verify if there are users already in Keycloak databases and remove them
	 * @param usersFile
	 * @return 
	 * @throws UsuarioNaoEncontradoException
	 */
	private Set<UserDTO> verifyList( Set<UserDTO> usersFile ) throws UsuarioNaoEncontradoException {
		
		log.info("Getting users from Keycloak");
		List<UserRepresentation> allUserKeycloak = userService.getUserAll(null, null);
		
		List<UserDTO> usersKeycloak = allUserKeycloak.stream()
				.map( s -> this.parseUserDTO( s ) )
				.collect( Collectors.toList() );
		
		log.info("Removing users that already there");
		usersFile.removeIf( usersKeycloak::contains );
		
		log.info( usersFile.size() + " users will be created with that file!" );
		
		return usersFile;
	}
	
	/**
	 * Send users to JMS queue
	 * @param users
	 */
	private void sendQueue( Set<UserDTO> users ) {
		
		log.info("Sending users to JMS queue!");
		
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
			log.error(line);
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
