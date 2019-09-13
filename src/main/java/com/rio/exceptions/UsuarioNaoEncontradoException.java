package com.rio.exceptions;

public class UsuarioNaoEncontradoException extends Exception {

	private static final long serialVersionUID = -2508177541193697117L;


	public UsuarioNaoEncontradoException(String cpf) {
		super("O Usuário sob o CPF " + cpf + " não está cadastrado!");
	}


	public UsuarioNaoEncontradoException(String cpf, Throwable cause) {
		super("O Usuário sob o CPF " + cpf + " não está cadastrado!", cause);
	}
	
}
