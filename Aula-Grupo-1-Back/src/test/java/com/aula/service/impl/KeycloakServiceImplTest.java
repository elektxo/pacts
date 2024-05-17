package com.aula.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.aula.dto.converter.UserConverter;
import com.aula.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class KeycloakServiceImplTest {
	
	@Mock
    private UserRepository userRepository;
    
	@Mock
	private UserConverter userConverter;
    
	@InjectMocks
	private KeycloakServiceImpl keycloackService;
	
	@Test
	void testDeleteuser() {
		//Given
		String userId = "id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f";
		
		//Mock calls
		Mockito.doNothing().when(userRepository).delete(userId);
		
		//When
		ResponseEntity<?> deleteUserResp = keycloackService.deleteUser(userId);
		//Then
		assertEquals(HttpStatus.NO_CONTENT, deleteUserResp.getStatusCode());
		assertEquals(null, deleteUserResp.getBody());
	}
}
