package com.aula.service;

import com.aula.dto.RequestContentDTO;
import com.aula.dto.ResponseContentDTO;
import com.aula.entity.Content;
import com.aula.entity.Course;
import com.aula.entity.CourseContent;
import com.aula.entity.CourseRegistration;
import com.aula.entity.CourseRegistrationContent;
import com.aula.entity.Reviewable;
import com.aula.repository.ContentRepository;
import com.aula.repository.CourseContentRepository;
import com.aula.repository.CourseRegistrationContentRepository;
import com.aula.repository.CourseRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;
    
    @Mock
    private CourseRepository courseRepository;
    
    @Mock
    private FileStorageService	fileStorageService;
    
    @Mock
	private CourseContentRepository courseConRepository;
    
	@Mock
	private CourseRegistrationContentRepository courseRegConRepository;

    @InjectMocks
    private ContentService contentService;

    private RequestContentDTO requestContentDTO;
    private Content content;
    private ResponseContentDTO responseContentDTO;
    private MockMultipartFile image;
    
    @BeforeEach
    void setUp() throws IOException {
        image = new MockMultipartFile("image", "test.png", "image/png", "test image content".getBytes());

        requestContentDTO = new RequestContentDTO("Test Title", "Test Description", 10, "id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f");
        
        content = Content.builder()
        		.id(1L)
                .title(requestContentDTO.getTitle())
                .description(requestContentDTO.getDescription())
                .estimatedHours(requestContentDTO.getEstimatedHours())
                .teacherId(requestContentDTO.getTeacherId())
                .imagePath("mediaFiles/content/" + image.getOriginalFilename())
                .comments(new ArrayList<>())
                .build();

        responseContentDTO = new ResponseContentDTO(
        		content.getId(), 
        		content.getTitle(), 
        		content.getDescription(), 
        		content.getEstimatedHours(), 
        		content.getImagePath(),
        		new ArrayList<>()
        		);
    }

    @Test
    void getAllContents_ShouldReturnAllContents() {
        when(contentRepository.findAll()).thenReturn(Arrays.asList(content));

        ResponseEntity<List<ResponseContentDTO>> response = contentService.getAllContents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());

        verify(contentRepository).findAll();
    }

    @Test
    void getContentById_WhenContentExists_ShouldReturnContent() {
        when(contentRepository.findById(anyLong())).thenReturn(Optional.of(content));

        ResponseEntity<ResponseContentDTO> response = contentService.getContentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(responseContentDTO, response.getBody());

        verify(contentRepository).findById(anyLong());
    }

    @Test
    void getContentById_WhenContentDoesNotExist_ShouldReturnNotFound() {
        when(contentRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<ResponseContentDTO> response = contentService.getContentById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(contentRepository).findById(anyLong());
    }

    @Test
    void createContent_ShouldCreateContentAndReturnSuccessMessage() throws IOException {
        
    	//Mock calls
    	Mockito.when(contentRepository.save(any(Content.class))).thenReturn(content);
    	Mockito.when(fileStorageService.store(image, "content")).thenReturn("mediaFiles/content/" + image.getOriginalFilename());
    	//When
        ResponseEntity<String> response = contentService.createContent(requestContentDTO, image);
        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("\"Content successfully created with id: " + content.getId() + "\"", response.getBody());

        verify(contentRepository).save(any(Content.class));
    }

    @Test
    void createContent_WhenIOExceptionOccurs_ShouldReturnInternalServerError() throws Exception {
        when(contentRepository.save(any(Content.class))).thenThrow(new RuntimeException("Fake runtime exception"));

        ResponseEntity<String> response = contentService.createContent(requestContentDTO, image);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("\"Error attempting to create content: Fake runtime exception\"", response.getBody());
    }
    
    @Test
    void deleteContent_WhenContentExists_ShouldReturnSuccessMessage() {
        //Given
		Reviewable reviewable = new Reviewable();
		reviewable.setId((long)1);
    	Course course = new Course();
		course.setId((long)1);
		course.setTitle("Java");
		course.setDescription("Introduction to Java");
		course.setPrice(20.20);
		course.setFree(false);
		course.setTeacherId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f");
		course.setImagePath("");
		course.setReviewable(reviewable);
		course.setCourseContents(new ArrayList<>());
		course.setRegistrations(new ArrayList<>());
		course.setComments(new ArrayList<>());
		CourseContent courseContent = new CourseContent();
		courseContent.setContent(content);
		courseContent.setCourse(course);
		List<CourseContent> courseContents = new ArrayList<>();
		courseContents.add(courseContent);
		//Add contents to course
		course.setCourseContents(courseContents);
		//Add relation to content
		content.setCourseContents(courseContents);
		List<CourseRegistration> courseRegistrations = new ArrayList<>();
		CourseRegistration courseReg = new CourseRegistration();
		courseReg.setId(1L);
		courseReg.setCourse(course);
		courseReg.setStudentId("c73644f1-b3d1-4443-b1ff-5b289f7e8fef");
		courseRegistrations.add(courseReg);
		List<CourseRegistrationContent> courseRegContents = new ArrayList<>();
		CourseRegistrationContent courseRegCon = new CourseRegistrationContent();
		courseRegCon.setId(1L);
		courseRegCon.setContent(content);
		courseRegCon.setCourseRegistration(courseReg);
		courseRegCon.setCompleted(false);
		courseRegContents.add(courseRegCon);
		//Add contents to registration
		courseReg.setCourseRegistrationContents(courseRegContents);
		//Add registrations to course
		course.setRegistrations(courseRegistrations);
		
        //Mock calls
        Mockito.when(contentRepository.findById(1L)).thenReturn(Optional.of(content));
        Mockito.doNothing().when(courseConRepository).delete(any(CourseContent.class));
        Mockito.doNothing().when(courseRegConRepository).delete(any(CourseRegistrationContent.class));
        Mockito.when(courseRegConRepository.findByCourseRegistrationAndContent(courseReg, content)).thenReturn(courseRegCon);
        
        
        //When
        ResponseEntity<String> response = contentService.deleteContent(1L);
        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"Content deleted successfully\""));
    }
    
    @Test
    void deleteContent_WhenExceptionOccurs_ShouldReturnInternalServerError() {
        doThrow(new RuntimeException("Fake Exception")).when(contentRepository).findById(anyLong());

        ResponseEntity<String> response = contentService.deleteContent(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody()
        		.contains("\"Error while attempting to delete content with id: 1. Fake Exception\""));
    }

    @Test
    void updateContent_WhenContentExists_ShouldUpdateContentAndReturnSuccessMessage() throws Exception {
        when(contentRepository.findById(anyLong())).thenReturn(Optional.of(content));
        when(contentRepository.save(any(Content.class))).thenReturn(content);

        ResponseEntity<String> response = contentService.updateContent(1L, requestContentDTO, image);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Contenido actualizado con éxito"));

        verify(contentRepository).findById(anyLong());
        verify(contentRepository).save(any(Content.class));
    }

    @Test
    void updateContent_WhenContentDoesNotExist_ShouldReturnNotFound() {
        when(contentRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<String> response = contentService.updateContent(1L, requestContentDTO, image);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(contentRepository).findById(anyLong());
    }

    @Test
    void updateContent_WhenIOExceptionOccurs_ShouldReturnBadRequest() throws Exception {
        when(contentRepository.findById(anyLong())).thenReturn(Optional.of(content));
        when(contentRepository.save(any(Content.class))).thenThrow(new RuntimeException("Fake RuntimeException"));

        ResponseEntity<String> response = contentService.updateContent(1L, requestContentDTO, image);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Error al actualizar el contenido:"));

        verify(contentRepository).findById(anyLong());
        verify(contentRepository).save(any(Content.class));
    }
    
    @Test
    void findTeacherContents() {
    	//Given
    	String teacherId = "id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f";
    	String fakeId = "fakeId";
    	String throwsException = "throwsException";
    	
    	List<ResponseContentDTO> dtos = List.of(responseContentDTO);
    	
    	//Mock calls
    	Mockito.when(contentRepository.findByTeacherId(teacherId)).thenReturn(List.of(content));
    	Mockito.when(contentRepository.findByTeacherId(fakeId)).thenReturn(new ArrayList<>());
    	Mockito.doThrow(new RuntimeException("Fake Exception")).when(contentRepository).findByTeacherId(throwsException);
    	
    	//When
    	ResponseEntity<?> findTeacherContentsResp = contentService.findTeacherContents(teacherId);
    	//Then
    	assertEquals(HttpStatus.OK, findTeacherContentsResp.getStatusCode());
		assertEquals(dtos, findTeacherContentsResp.getBody());
		//When
		findTeacherContentsResp = contentService.findTeacherContents(fakeId);
		//Then
    	assertEquals(HttpStatus.NOT_FOUND, findTeacherContentsResp.getStatusCode());
		assertEquals(null, findTeacherContentsResp.getBody());
		//When
		findTeacherContentsResp = contentService.findTeacherContents(throwsException);
		//Then
    	assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, findTeacherContentsResp.getStatusCode());
		assertEquals(findTeacherContentsResp.getBody(), "\"Error attempting to find contents of theacher with id: " + throwsException + ". Fake Exception\"");

    }
    
    @Test
    void searchContent() {
    	//Given
		String filterMatch = "AV";
		String filterMiss = "ZZZZ";

		List<ResponseContentDTO> dtos = List.of(responseContentDTO);
		
		//Mock calls
		Mockito.when(contentRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(filterMatch, filterMatch)).thenReturn(List.of(content));
		Mockito.when(contentRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(filterMiss, filterMiss)).thenReturn(new ArrayList<>());
		
		//When
		ResponseEntity<?> searchResponse = contentService.searchContent(filterMatch);
		//Then
		assertEquals(HttpStatus.OK, searchResponse.getStatusCode());
		assertEquals(dtos, searchResponse.getBody());
		//When
		searchResponse = contentService.searchContent(filterMiss);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, searchResponse.getStatusCode());
		assertEquals("\"There are not such content registered\"", searchResponse.getBody());
    }
    
    @Test
    void testGetContentsByCourseId() {
    	//Given
    	Long courseId = 1L;
    	Long notFoundId = 2L;
    	Reviewable reviewable = new Reviewable();
		reviewable.setId((long)1);
    	Course course = new Course();
		course.setId((long)1);
		course.setTitle("Java");
		course.setDescription("Introduction to Java");
		course.setPrice(20.20);
		course.setFree(false);
		course.setTeacherId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f");
		course.setImagePath("");
		course.setReviewable(reviewable);
		course.setCourseContents(new ArrayList<>());
		course.setRegistrations(new ArrayList<>());
		course.setComments(new ArrayList<>());
		CourseContent courseContent = new CourseContent();
		courseContent.setContent(content);
		courseContent.setCourse(course);
		List<CourseContent> courseContents = new ArrayList<>();
		courseContents.add(courseContent);
		//Add contents to course
		course.setCourseContents(courseContents);
		//Add relation to content
		content.setCourseContents(courseContents);
		//List of dtos for the response
		List<ResponseContentDTO> dtos = List.of(responseContentDTO);
		
    	//Mock calls
    	Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
    	Mockito.when(courseRepository.findById(notFoundId)).thenReturn(Optional.ofNullable(null));
    	
    	//When
		ResponseEntity<?> contentsResponse = contentService.getContentsByCourseId(courseId);
		//Then
		assertEquals(HttpStatus.OK, contentsResponse.getStatusCode());
		assertEquals(dtos, contentsResponse.getBody());
		//When
		contentsResponse = contentService.getContentsByCourseId(notFoundId);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, contentsResponse.getStatusCode());
		assertEquals(null, contentsResponse.getBody());
    }
}
