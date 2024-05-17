package com.aula.service;

import com.aula.dto.UserDTO;
import com.aula.dto.UserRequestDTO;
import com.aula.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IKeycloakService {
    ResponseEntity<List<UserResponseDTO>> getAllUsers();
    ResponseEntity<UserResponseDTO> getUserById(String id);
    ResponseEntity<List<UserResponseDTO>> getUserByUsername(String username);
    ResponseEntity<List<UserResponseDTO>> getUserByEmail(String email);
    ResponseEntity<UserResponseDTO> createUser(UserDTO userDTO);
    ResponseEntity<Void> deleteUser(String userId);
    ResponseEntity<UserResponseDTO> updateUser(String userId, UserRequestDTO userDTO);
}
