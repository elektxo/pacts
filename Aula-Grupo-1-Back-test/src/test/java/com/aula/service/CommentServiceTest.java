package com.aula.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.mock.web.MockMultipartFile;

import com.aula.dto.CommentRequest;
import com.aula.dto.CommentResponse;
import com.aula.dto.CourseDTO;
import com.aula.entity.Comment;
import com.aula.entity.Content;
import com.aula.entity.Course;
import com.aula.entity.Reviewable;
import com.aula.repository.CommentRepository;
import com.aula.repository.ContentRepository;
import com.aula.repository.CourseRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceTest {

	@Mock
	private CourseRepository courseRepository;
	
	@Mock
	private CommentRepository commentRepository;
	
	@Mock
	private ContentRepository contentRepository;
	
	@InjectMocks
	private CommentService commentService;

	private MockMultipartFile image;
	private Reviewable reviewable;
	private Course course;
	private Content content;
	private CourseDTO courseDto;
	private Comment comment;
	private CommentRequest commentRequest;
	private CommentResponse commentResponse;
	
	@BeforeEach
	void setUp() {
		//teacher id: id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f
		//student id: c73644f1-b3d1-4443-b1ff-5b289f7e8fef
		image = new MockMultipartFile("image", "test.png", "image/png", "test image content".getBytes());
		courseDto = new CourseDTO();
		courseDto.setTitle("Java");
		courseDto.setDescription("Introduction to Java");
		courseDto.setPrice(20.20);
		courseDto.setFree(false);
		courseDto.setTeacherId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f");
		
		reviewable = new Reviewable();
		reviewable.setId((long)1);
		
		course = new Course();
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
		
		content = new Content();
		content.setId((long)1);
		content.setTitle("Java");
		content.setDescription("Introduction to Java");
		content.setTeacherId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f");
		content.setImagePath(null);
		content.setEstimatedHours(2);
		
		comment = new Comment();
		comment.setId(1L);
		comment.setContent(content);
		comment.setText("Test text");
		comment.setCreatorUsername("Test username");
		LocalDateTime localDateTime = LocalDateTime.parse("2024-01-01T00:00:00.000000");
		comment.setCreatedAt(localDateTime);
		comment.setUpdatedAt(localDateTime);

		commentRequest = new CommentRequest();
		commentRequest.setText("Test text");
		commentRequest.setCreatorUsername("Test username");
		
		commentResponse = new CommentResponse(
			comment.getId(), 
			comment.getText(), 
			comment.getCreatorUsername(), 
			comment.getCreatedAt(), 
			comment.getUpdatedAt()
		);
	}
	
	@Test
	void testCreateContentComment() {
		//Given
		Long contentId = 1L;
		Long notFoundId = 2L;
		Long throwExceptionId = 3L;
		
		//Mock calls
		Mockito.when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));
		Mockito.when(contentRepository.findById(notFoundId)).thenReturn(Optional.ofNullable(null));
		Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);
		Mockito.doThrow(new RuntimeException("Fake Exception")).when(contentRepository).findById(throwExceptionId);
		
		//When
		ResponseEntity<?> createContentCommentResp = commentService.createContentComment(commentRequest, contentId);
		//Then
		assertEquals(HttpStatus.OK, createContentCommentResp.getStatusCode());
		assertEquals(commentResponse, createContentCommentResp.getBody());
		//When
		createContentCommentResp = commentService.createContentComment(commentRequest, notFoundId);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, createContentCommentResp.getStatusCode());
		assertEquals(null, createContentCommentResp.getBody());
		//When
		createContentCommentResp = commentService.createContentComment(commentRequest, throwExceptionId);
		//Then
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createContentCommentResp.getStatusCode());
		assertEquals(null, createContentCommentResp.getBody());
	}
	
	@Test
	void testCreateCourseComment() {
		//Given
		Long courseId = 1L;
		Long notFoundId = 2L;
		Long throwExceptionId = 3L;
		
		//Mock calls
		Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
		Mockito.when(courseRepository.findById(notFoundId)).thenReturn(Optional.ofNullable(null));
		Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);
		Mockito.doThrow(new RuntimeException("Fake Exception")).when(courseRepository).findById(throwExceptionId);
		
		//When
		ResponseEntity<?> createCourseCommentResp = commentService.createCourseComment(commentRequest, courseId);
		//Then
		assertEquals(HttpStatus.OK, createCourseCommentResp.getStatusCode());
		assertEquals(commentResponse, createCourseCommentResp.getBody());
		//When
		createCourseCommentResp = commentService.createCourseComment(commentRequest, notFoundId);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, createCourseCommentResp.getStatusCode());
		assertEquals(null, createCourseCommentResp.getBody());
		//When
		createCourseCommentResp = commentService.createCourseComment(commentRequest, throwExceptionId);
		//Then
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createCourseCommentResp.getStatusCode());
		assertEquals(null, createCourseCommentResp.getBody());
	}
	
	@Test
	void testUpdateCommentById() {
		//Given
		Long commentId = 1L;
		Long notFoundId = 2L;
		Long throwExceptionId = 3L;
		
		//Mock calls
		Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		Mockito.when(commentRepository.findById(notFoundId)).thenReturn(Optional.ofNullable(null));
		Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);
		Mockito.doThrow(new RuntimeException("Fake Exception")).when(commentRepository).findById(throwExceptionId);
		
		//When
		ResponseEntity<?> updateCommentResp = commentService.updateCommentById(commentRequest, commentId);
		//Then
		assertEquals(HttpStatus.OK, updateCommentResp.getStatusCode());
		assertEquals("\"Comentario actualizado con éxito con ID: 1\"", updateCommentResp.getBody());
		//When
		updateCommentResp = commentService.updateCommentById(commentRequest, notFoundId);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, updateCommentResp.getStatusCode());
		assertEquals(null, updateCommentResp.getBody());
		//When
		updateCommentResp = commentService.updateCommentById(commentRequest, throwExceptionId);
		//Then
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateCommentResp.getStatusCode());
		assertEquals("\"Error al actualizar el comentario: Fake Exception\"", updateCommentResp.getBody());
	}
	
	@Test
	void testDeleteCommentById() {
		//Given
		Long commentId = 1L;
		Long throwExceptionId = 3L;
		
		//Mock calls
		Mockito.doThrow(new RuntimeException("Fake Exception")).when(commentRepository).deleteById(throwExceptionId);
		Mockito.doNothing().when(commentRepository).deleteById(commentId);
		
		//When
		ResponseEntity<?> deleteCommentResp = commentService.deleteCommentById(commentId);
		//Then
		assertEquals(HttpStatus.OK, deleteCommentResp.getStatusCode());
		assertEquals("\"Comentario borrado con éxito con ID: " + commentId + "\"", deleteCommentResp.getBody());
		//When
		deleteCommentResp = commentService.deleteCommentById(throwExceptionId);
		//Then
		 assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, deleteCommentResp.getStatusCode());
		 assertEquals("\"Error al eliminar el comentario: Fake Exception\"", deleteCommentResp.getBody());
	}
	
	@Test
	void testGetCommentById() {
		//Given
		Long commentId = 1L;
		Long notFoundId = 2L;
		Long throwExceptionId = 3L;
		
		//Mock calls
		Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		Mockito.when(commentRepository.findById(notFoundId)).thenReturn(Optional.ofNullable(null));
		Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);
		Mockito.doThrow(new RuntimeException("Fake Exception")).when(commentRepository).findById(throwExceptionId);
		
		//When
		ResponseEntity<?> getCommentResp = commentService.getCommentById(commentId);
		//Then
		assertEquals(HttpStatus.OK, getCommentResp.getStatusCode());
		assertEquals(commentResponse, getCommentResp.getBody());
		//When
		getCommentResp = commentService.getCommentById(notFoundId);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, getCommentResp.getStatusCode());
		assertEquals(null, getCommentResp.getBody());
		//When
		getCommentResp = commentService.getCommentById(throwExceptionId);
		//Then
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getCommentResp.getStatusCode());
		assertEquals(null, getCommentResp.getBody());
	}
	
	@Test
	void testGetCommentsByCourseId() {
		//Given
		Long courseId = 1L;
		Long notFoundId = 2L;
		Long throwExceptionId = 3L;
		
		//Mock calls
		Mockito.when(commentRepository.findByCourseId(courseId)).thenReturn(List.of(comment));
		Mockito.when(commentRepository.findByCourseId(notFoundId)).thenReturn(new ArrayList<>());
		Mockito.doThrow(new RuntimeException("Fake Exception")).when(commentRepository).findByCourseId(throwExceptionId);
		
		//When
		ResponseEntity<?> getCommentsResp = commentService.getCommentsByCourseId(courseId);
		//Then
		assertEquals(HttpStatus.OK, getCommentsResp.getStatusCode());
		assertEquals(List.of(commentResponse), getCommentsResp.getBody());
		//When
		getCommentsResp = commentService.getCommentsByCourseId(notFoundId);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, getCommentsResp.getStatusCode());
		assertEquals(null, getCommentsResp.getBody());
		//When
		getCommentsResp = commentService.getCommentsByCourseId(throwExceptionId);
		//Then
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getCommentsResp.getStatusCode());
		assertEquals(null, getCommentsResp.getBody());
	}
	
	@Test
	void testGetCommentsByContentId() {
		//Given
		Long contentId = 1L;
		Long notFoundId = 2L;
		Long throwExceptionId = 3L;
		
		//Mock calls
		Mockito.when(commentRepository.findByContentId(contentId)).thenReturn(List.of(comment));
		Mockito.when(commentRepository.findByContentId(notFoundId)).thenReturn(new ArrayList<>());
		Mockito.doThrow(new RuntimeException("Fake Exception")).when(commentRepository).findByContentId(throwExceptionId);
		
		//When
		ResponseEntity<?> getCommentsResp = commentService.getCommentsByContentId(contentId);
		//Then
		assertEquals(HttpStatus.OK, getCommentsResp.getStatusCode());
		assertEquals(List.of(commentResponse), getCommentsResp.getBody());
		//When
		getCommentsResp = commentService.getCommentsByContentId(notFoundId);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, getCommentsResp.getStatusCode());
		assertEquals(null, getCommentsResp.getBody());
		//When
		getCommentsResp = commentService.getCommentsByContentId(throwExceptionId);
		//Then
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getCommentsResp.getStatusCode());
		assertEquals(null, getCommentsResp.getBody());
	}
}
