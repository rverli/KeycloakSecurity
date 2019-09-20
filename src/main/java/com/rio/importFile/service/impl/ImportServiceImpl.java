package com.rio.importFile.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rio.importFile.jms.SenderJms;
import com.rio.importFile.service.ImportService;

@Service
public class ImportServiceImpl implements ImportService {
	
	@Autowired
	private SenderJms sender;
	
	@Autowired
	private FileStorageServiceImpl fileStorageService;
		
	@Value("${destination.queue}")
	private String destinationQueue;
	
	@Value("${file.upload-dir}")
	private String UPLOAD_DIR;
	
	public void importFile( MultipartFile file ) throws Exception {
		
		String fileName = fileStorageService.storeFile(file);
		
		File csvFile = new File(UPLOAD_DIR + "/" + fileName);
		
		if ( csvFile.isFile() && csvFile.exists() ) {
			
			String line = null;
			
			try {
				BufferedReader csvReader = new BufferedReader(new FileReader( csvFile ));
				while ((line = csvReader.readLine()) != null) {					
					sender.send(destinationQueue, line);
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
}
