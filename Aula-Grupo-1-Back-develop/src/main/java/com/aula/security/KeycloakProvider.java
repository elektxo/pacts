package com.aula.security;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;

public class KeycloakProvider {
    private static final String SERVER_URL = "http://localhost:8080";
    private static final String REALM_NAME = "virtual-classroom";
    private static final String CLIENT_ID = "api-client-admin";
    private static final String CLIENT_SECRET = "QEMZ7pebZ1EyTeJ0Zde5hNGl0TsCLla5";

    private KeycloakProvider() {}

    public static RealmResource getRealmResource() {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(REALM_NAME)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();
            return keycloak.realm(REALM_NAME);
    }

    public static UsersResource getUserResource() {
        RealmResource realmResource = getRealmResource();
        return realmResource.users();
    }
}