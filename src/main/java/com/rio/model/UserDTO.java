package com.rio.model;

import java.io.Serializable;
import java.util.List;

public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String password;	
	private List<String> roles;
	
	public UserDTO(){
		super();
	}
	
	public UserDTO(
			String userId, 
			String username, 
			String email, 
			String firstName, 
			String lastName) {
		super();
		this.id = userId;
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmailAddress(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getId() {
		return id;
	}
	public void setId(String userId) {
		this.id = userId;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}		
}
