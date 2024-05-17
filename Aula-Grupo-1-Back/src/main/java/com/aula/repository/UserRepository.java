package com.aula.repository;

import com.aula.security.KeycloakProvider;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    public List<UserRepresentation> findAll() {
        return KeycloakProvider.getUserResource().list();
    }

    public Optional<UserRepresentation> findById(String id) {
        return Optional.ofNullable(KeycloakProvider.getUserResource().get(id).toRepresentation());
    }

    public List<UserRepresentation> findByEmail(String email, Boolean exact) {
        return KeycloakProvider.getUserResource().searchByEmail(email, exact);
    }

    public List<UserRepresentation> findByUsername (String username, Boolean exact) {
        return KeycloakProvider.getUserResource().searchByUsername(username, exact);
    }

    public void delete (String id) {
        KeycloakProvider.getUserResource().get(id).remove();
    }
}
