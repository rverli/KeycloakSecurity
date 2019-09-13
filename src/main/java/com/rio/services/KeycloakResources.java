package com.rio.services;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.stereotype.Service;

@Service
public final class KeycloakResources {
	
	private static UsersResource usersResourceInstance;
	private static RealmResource realmResourceInstance;	
	private static Keycloak keycloakResourceInstance;
	
	public static synchronized Keycloak getKeycloakResourceInstance(String AUTHURL, String REALM) {
		
		if ( keycloakResourceInstance == null ) {
			keycloakResourceInstance = KeycloakResources.getKeycloakResource(AUTHURL, REALM);
		}
		return keycloakResourceInstance;		
	}
	
	public static synchronized UsersResource getUsersResourceInstance(String AUTHURL, String REALM) {
		
		if ( usersResourceInstance == null ) {
			usersResourceInstance = KeycloakResources.getKeycloakUserResource(AUTHURL, REALM);
		}
		return usersResourceInstance;		
	}
	
	public static synchronized RealmResource getRealmResourceInstance(String AUTHURL, String REALM) {
		
		if ( realmResourceInstance == null ) {
			realmResourceInstance = KeycloakResources.getKeycloakRealmResource(AUTHURL, REALM);
		}
		return realmResourceInstance;		
	}
	
	private static Keycloak getKeycloakResource(String AUTHURL, String REALM) {

		return KeycloakBuilder.builder()
				.serverUrl(AUTHURL)
				.realm("master")
				.username("admin")
				.password("admin")
				.clientId("admin-cli")				
				.resteasyClient( new ResteasyClientBuilder().connectionPoolSize(10).build() )
				.build();
	}
	
	private static UsersResource getKeycloakUserResource(String AUTHURL, String REALM) {
		
		RealmResource realmResource = KeycloakResources.getKeycloakResource(AUTHURL, REALM).realm(REALM);
		UsersResource userRessource = realmResource.users();

		return userRessource;
	}

	private static RealmResource getKeycloakRealmResource(String AUTHURL, String REALM) {
		return KeycloakResources.getKeycloakResource(AUTHURL, REALM).realm(REALM);
	}
}
