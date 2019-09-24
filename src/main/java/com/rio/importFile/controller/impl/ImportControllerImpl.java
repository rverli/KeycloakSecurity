package com.rio.importFile.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rio.importFile.controller.ImportController;
import com.rio.importFile.service.ImportService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/v1/file")
@Slf4j
public class ImportControllerImpl implements ImportController {

    @Autowired
    private ImportService importService;

    @PostMapping("/import")
    public void importFile(@RequestParam("file") MultipartFile multipartFile) throws Exception {

    	try {
    		importService.importFile( multipartFile );    		
		} catch (Exception e) {
			log.error(e.getMessage());
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
			log.error(e.getMessage());
			throw e;
		}
    }
}
