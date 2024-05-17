package com.aula.controller;

import com.aula.dto.UserDTO;
import com.aula.dto.UserRequestDTO;
import com.aula.dto.UserResponseDTO;
import com.aula.service.impl.KeycloakServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/users")
public class UserController {
    private final KeycloakServiceImpl keycloakService;

    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return keycloakService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String id) {
        return keycloakService.getUserById(id);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<List<UserResponseDTO>> searchUserByUsername(@PathVariable String username) {
        return keycloakService.getUserByUsername(username);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<UserResponseDTO>> searchUserByEmail(@PathVariable String email) {
        return keycloakService.getUserByEmail(email);
    }

    @PostMapping()
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
        return keycloakService.createUser(userDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String id, @RequestBody @Valid UserRequestDTO userDTO) {
        return keycloakService.updateUser(id, userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        return keycloakService.deleteUser(id);
    }
}