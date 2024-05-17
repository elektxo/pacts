package com.aula.dto.converter;

import com.aula.dto.UserResponseDTO;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserResponseDTO mapToResponse(UserRepresentation user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isEmailVerified());
    }
}
