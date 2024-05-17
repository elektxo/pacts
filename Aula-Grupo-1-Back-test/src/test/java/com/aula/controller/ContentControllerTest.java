package com.aula.controller;

import com.aula.dto.RequestContentDTO;
import com.aula.dto.ResponseContentDTO;
import com.aula.service.ContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentControllerTest {

    @Mock
    private ContentService contentService;

    @InjectMocks
    private ContentController contentController;

    private RequestContentDTO requestContentDTO;
    private ResponseContentDTO responseContentDTO;
    private MockMultipartFile image;

    @BeforeEach
    void setUp() {
        image = new MockMultipartFile("image", "test.png", "image/png", "test image content".getBytes());
        requestContentDTO = new RequestContentDTO();
        requestContentDTO.setTitle("Test Title");
        requestContentDTO.setDescription("Test Description");
        requestContentDTO.setEstimatedHours(10);
        responseContentDTO = new ResponseContentDTO();
        responseContentDTO.setId((long) 1);
        responseContentDTO.setTitle("Test Title");
        responseContentDTO.setDescription("Test Description");
        responseContentDTO.setEstimatedHours(10);
        responseContentDTO.setImagePath("testpath");
    }

    @Test
    void whenCreateContentCalled_thenDelegateToService() {
        when(contentService.createContent(requestContentDTO, image))
                .thenReturn(ResponseEntity.ok("Contenido creado con éxito"));

        ResponseEntity<String> response = contentController.createContent(requestContentDTO, image);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Contenido creado con éxito", response.getBody());
        verify(contentService).createContent(requestContentDTO, image);
    }

    @Test
    void whenGetAllContentsCalled_thenDelegateToService() {
        when(contentService.getAllContents())
                .thenReturn(ResponseEntity.ok(Collections.singletonList(responseContentDTO)));

        ResponseEntity<List<ResponseContentDTO>> response = contentController.getAllContents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(contentService).getAllContents();
    }

    @Test
    void whenGetContentByIdCalled_thenDelegateToService() {
        when(contentService.getContentById(anyLong()))
                .thenReturn(ResponseEntity.ok(responseContentDTO));

        ResponseEntity<ResponseContentDTO> response = contentController.getContentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(contentService).getContentById(anyLong());
    }

    @Test
    void whenDeleteContentCalled_thenDelegateToService() {
        when(contentService.deleteContent(anyLong()))
                .thenReturn(ResponseEntity.ok("Contenido eliminado con éxito"));

        ResponseEntity<String> response = contentController.deleteContent(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Contenido eliminado con éxito", response.getBody());
        verify(contentService).deleteContent(anyLong());
    }

    @Test
    void whenUpdateContentCalled_thenDelegateToService() {
        when(contentService.updateContent((long)1, requestContentDTO, image))
                .thenReturn(ResponseEntity.ok("Contenido actualizado con éxito"));

        ResponseEntity<String> response = contentController.updateContent((long)1, requestContentDTO, image);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Contenido actualizado con éxito", response.getBody());
        verify(contentService).updateContent((long)1, requestContentDTO, image);
    }
}
