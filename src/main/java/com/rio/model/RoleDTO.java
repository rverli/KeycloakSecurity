package com.rio.model;

import java.io.Serializable;

import org.keycloak.representations.idm.RoleRepresentation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class RoleDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	
	public RoleDTO( RoleRepresentation roleRepresentation ) {
		super();
		this.name = roleRepresentation.getName();
		this.description = roleRepresentation.getDescription();
	}
}
