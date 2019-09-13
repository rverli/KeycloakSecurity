package com.rio.exceptions;

public class UsuarioNaoEncontradoException extends Exception {

	private static final long serialVersionUID = -2508177541193697117L;


	public UsuarioNaoEncontradoException(String username) {
		super("O Usuário com username " + username + " não está cadastrado!");
	}


	public UsuarioNaoEncontradoException(String username, Throwable cause) {
		super("O Usuário com username " + username + " não está cadastrado!", cause);
	}
	
}
