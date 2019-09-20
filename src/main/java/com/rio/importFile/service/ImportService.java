package com.rio.importFile.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.rio.exceptions.UsuarioNaoEncontradoException;

public interface ImportService {
	void importFile( MultipartFile file ) throws IOException, UsuarioNaoEncontradoException;
}
