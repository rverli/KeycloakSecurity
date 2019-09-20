package com.rio.importFile.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
	
	void importFile( MultipartFile file ) throws Exception;
}
