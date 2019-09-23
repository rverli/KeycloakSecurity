package com.rio.importFile.controller;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface ImportController {

	/**
	 * Import users from file 
	 * @param multipartFile
	 * @throws Exception
	 */
    void importFile(@RequestParam("file") MultipartFile multipartFile) throws Exception;
    
    /**
     * Import users from multiple file
     * @param multipartFiles
     * @throws Exception
     */
    void importMultipleFiles(@RequestParam("files") MultipartFile[] multipartFiles ) throws Exception;
}
