package com.rio.exceptions;

public class KeycloakException extends Exception {

	private static final long serialVersionUID = -2508177541193697117L;


	public KeycloakException(int httpResponse) {
		super("Erro ao gravar usuário! [http response code = " + httpResponse + "]");
	}
}
