package com.aula.service.impl;

import com.aula.dto.UserDTO;
import com.aula.dto.UserRequestDTO;
import com.aula.dto.UserResponseDTO;
import com.aula.dto.converter.UserConverter;
import com.aula.exceptions.CreationUserException;
import com.aula.exceptions.DuplicateResourceException;
import com.aula.repository.UserRepository;
import com.aula.security.KeycloakProvider;
import com.aula.service.IKeycloakService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements IKeycloakService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakServiceImpl.class);
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    private List<RoleRepresentation> validateRoles(Set<String> roles) {
        RolesResource rolesResource = KeycloakProvider.getRealmResource().roles();
        return roles.stream().map(roleName -> {
                    try {
                        return rolesResource.get(roleName).toRepresentation();
                    } catch (NotFoundException ex) {
                        log.error("Role '{}' not found", roleName);
                        throw new NotFoundException("Role not found: " + roleName);
                    }
                }).toList();
    }

    @Override
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserRepresentation> users = userRepository.findAll();

//      Map into response DTO
        List<UserResponseDTO> userResponses = users.stream()
                .map(userConverter::mapToResponse)
                .toList();

        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponseDTO> getUserById(String id) {
        UserRepresentation user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("User not found with id: %1s", id)));
        return new ResponseEntity<>(userConverter.mapToResponse(user), HttpStatus.OK);
    }
    
    public UserRepresentation getUserRepresentationById(String id) {
    	 Optional<UserRepresentation> optUser = userRepository.findById(id);
    	 if (!optUser.isPresent()) {
    		return null;
    	 }
    	 return optUser.get();
    }

    @Override
    public ResponseEntity<List<UserResponseDTO>> getUserByUsername(String username) {
        List<UserRepresentation> users = userRepository.findByUsername(username, true);
        if (users.isEmpty()) throw new NotFoundException("User not found");

        List<UserResponseDTO> userResponses = users.stream()
                .map(userConverter::mapToResponse)
                .toList();

        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserResponseDTO>> getUserByEmail(String email) {
        List<UserRepresentation> users = userRepository.findByEmail(email, true);
        if (users.isEmpty()) throw new NotFoundException("User not found");

        List<UserResponseDTO> userResponses = users.stream()
                .map(userConverter::mapToResponse)
                .toList();

        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponseDTO> createUser(UserDTO userDTO) {
        UsersResource usersResource = KeycloakProvider.getUserResource();

        UserRepresentation user = new UserRepresentation();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setEnabled(true);
        user.setEmailVerified(false);

        try (Response response = usersResource.create(user)) {
            int status = response.getStatus();

            if (status == 409) throw new DuplicateResourceException("User already exists");
            if (status == 400) throw new BadRequestException("Invalid Request");
            if (status != 201) throw new CreationUserException("Error creating user, please contact the administrator.");

            // Get user id from the response
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            // Assign roles to the user if the roles array its provided
            Set<String> roles = userDTO.getRoles();
            if (roles != null && !roles.isEmpty()) {
                RealmResource realmResource = KeycloakProvider.getRealmResource();
                List<RoleRepresentation> rolesRepresentation = validateRoles(userDTO.getRoles());
                UserResource userResource = realmResource.users().get(userId);
                userResource.roles().realmLevel().add(rolesRepresentation);
            }

            // Set password for the user
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
            credentialRepresentation.setValue(userDTO.getPassword());
            usersResource.get(userId).resetPassword(credentialRepresentation);

            return new ResponseEntity<>(userConverter.mapToResponse(user), HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<UserResponseDTO> updateUser(String userId, UserRequestDTO userDTO) {
        UserRepresentation savedUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("User not found with id: %1s", userId)));

        log.info("User dto info: {} {} {}", userDTO.firstname(), userDTO.lastname(), userDTO.password());
        if (userDTO.firstname() != null && !(userDTO.firstname().isEmpty())) {
            savedUser.setFirstName(userDTO.firstname());
        }

        if (userDTO.lastname() != null && (!userDTO.lastname().isEmpty())) {
            savedUser.setLastName(userDTO.lastname());
        }

        if (userDTO.password() != null && (!userDTO.password().isEmpty())) {
            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(OAuth2Constants.PASSWORD);
            credentialRepresentation.setValue(userDTO.password());

            savedUser.setCredentials(Collections.singletonList(credentialRepresentation));
        }

        savedUser.setEnabled(true);
        savedUser.setEmailVerified(true);

        Set<String> roles = userDTO.roles();
        if (roles != null && !roles.isEmpty()) {
            RealmResource realmResource = KeycloakProvider.getRealmResource();
            List<RoleRepresentation> rolesRepresentation = validateRoles(userDTO.roles());
            UserResource userResource = realmResource.users().get(userId);
            userResource.roles().realmLevel().add(rolesRepresentation);
        }

        UserResource usersResource = KeycloakProvider.getUserResource().get(userId);
        usersResource.update(savedUser);

        return new ResponseEntity<>(userConverter.mapToResponse(savedUser), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteUser(String userId) {
        userRepository.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
