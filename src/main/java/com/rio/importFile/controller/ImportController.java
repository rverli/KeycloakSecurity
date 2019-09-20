package com.rio.importFile.controller;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface ImportController {

    void importFile(@RequestParam("file") MultipartFile multipartFile) throws Exception;
    
    void importMultipleFiles(@RequestParam("files") MultipartFile[] multipartFiles ) throws Exception;
}
