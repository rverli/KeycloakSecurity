package com.rio.exceptions;

public class UsuarioJaCadastradoException extends Exception {

	private static final long serialVersionUID = 5359100670712412994L;


	public UsuarioJaCadastradoException(String cpf) {
		super("O cidadão sob o CPF " + cpf + " já foi cadastrado anteriormente");
	}


	public UsuarioJaCadastradoException(String cpf, Throwable cause) {
		super("O cidadão sob o CPF " + cpf + " já foi cadastrado anteriormente", cause);
	}
}
