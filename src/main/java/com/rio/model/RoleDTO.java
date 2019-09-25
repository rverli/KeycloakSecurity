package com.rio.model;

import java.io.Serializable;

import org.keycloak.representations.idm.RoleRepresentation;

public class RoleDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	
	public RoleDTO() {
		super();
	}

	public RoleDTO( RoleRepresentation roleRepresentation ) {
		super();
		this.name = roleRepresentation.getName();
		this.description = roleRepresentation.getDescription();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
