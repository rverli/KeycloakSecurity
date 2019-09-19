package com.rio.fileupload.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rio.fileupload.service.ImportService;

@RestController
@RequestMapping(value = "/v1/file")
public class ImportController {

    private static final Logger logger = LoggerFactory.getLogger(ImportController.class);

    @Autowired
    private ImportService importService;

    @PostMapping("/import")
    public void importFile(@RequestParam("file") MultipartFile multipartFile) throws Exception {

    	try {
    		importService.importFile( multipartFile );    		
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
    }
    
    @PostMapping("/importMultipleFiles")
    public void importMultipleFiles(@RequestParam("files") MultipartFile[] multipartFiles ) throws Exception {
        
    	try {
    		for (MultipartFile multipartFile : multipartFiles) {
    			importService.importFile( multipartFile );
    		}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
    }
}
