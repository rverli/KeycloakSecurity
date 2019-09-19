package com.rio.exceptions;

public class UsuarioNaoEncontradoException extends Exception {

	private static final long serialVersionUID = -2508177541193697117L;


	public UsuarioNaoEncontradoException(String field) {
		super("O Usuário não está cadastrado com o campo -> [" + field + "]");
	}

	public UsuarioNaoEncontradoException(String field, Throwable cause) {
		super("O Usuário não está cadastrado com o campo -> [" + field + "]", cause);		
	}
}
