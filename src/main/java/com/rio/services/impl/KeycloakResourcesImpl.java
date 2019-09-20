package com.rio.services.impl;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.stereotype.Service;

import com.rio.services.KeycloakResources;

@Service
public class KeycloakResourcesImpl implements KeycloakResources {
	
	public Keycloak getKeycloakResourceInstance(String AUTHURL, String REALM) {		
		return this.getKeycloakResource(AUTHURL, REALM);		
	}
	
	public UsersResource getUsersResourceInstance(String AUTHURL, String REALM) {
		return this.getKeycloakUserResource(AUTHURL, REALM);		
	}
	
	public RealmResource getRealmResourceInstance(String AUTHURL, String REALM) {
		return this.getKeycloakRealmResource(AUTHURL, REALM);
	}
	
	private Keycloak getKeycloakResource(String AUTHURL, String REALM) {

		ResteasyClient client = new ResteasyClientBuilder().connectionPoolSize(10).build();
		
		return KeycloakBuilder.builder()
				.serverUrl(AUTHURL)
				.realm("master")
				.username("admin")
				.password("admin")
				.clientId("admin-cli")				
				.resteasyClient( client )
				.build();
	}
	
	private UsersResource getKeycloakUserResource(String AUTHURL, String REALM) {
		
		RealmResource realmResource = this.getKeycloakResource(AUTHURL, REALM).realm(REALM);
		UsersResource userRessource = realmResource.users();

		return userRessource;
	}

	private RealmResource getKeycloakRealmResource(String AUTHURL, String REALM) {
		return this.getKeycloakResource(AUTHURL, REALM).realm(REALM);
	}
}
