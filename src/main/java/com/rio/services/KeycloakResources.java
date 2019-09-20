package com.rio.services;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;

public interface KeycloakResources {
	
	Keycloak getKeycloakResourceInstance();
	
	UsersResource getUsersResourceInstance();
	
	RealmResource getRealmResourceInstance();
}
