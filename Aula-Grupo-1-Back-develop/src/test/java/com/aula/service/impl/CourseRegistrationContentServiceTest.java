package com.aula.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.aula.dto.ContentCompletedDTO;
import com.aula.dto.converter.BaseConverter;
import com.aula.entity.Content;
import com.aula.entity.Course;
import com.aula.entity.CourseRegistration;
import com.aula.entity.CourseRegistrationContent;
import com.aula.entity.Reviewable;
import com.aula.repository.BaseRepository;
import com.aula.repository.ContentRepository;
import com.aula.repository.CourseRegistrationContentRepository;
import com.aula.repository.CourseRegistrationRepository;
import com.aula.repository.CourseRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CourseRegistrationContentServiceTest {
	
	@InjectMocks
	private CourseRegistrationContentService courseRegConService;
	
	@Mock
	private CourseRegistrationRepository courseRegistrationRepository;
	
	@Mock
	private CourseRegistrationContentRepository courseRegConRepository;
	
	@Mock
	private CourseRepository courseRepository;
	
	@Mock
	private ContentRepository contentRepository;
	
	@Mock 
	BaseRepository<CourseRegistrationContent, Long> baseRepository;
	
	@Mock 
	BaseConverter<ContentCompletedDTO, CourseRegistrationContent> baseConverter;
	
	@Test
	void testComplete() {
		//Given
		String studentId = "c73644f1-b3d1-4443-b1ff-5b289f7e8fef";
		String studentMissesId = "misses";
		Long contentId = 1L;
		Long courseNotFoundId = 2L;
		Long contentNotFoundId = 2L;
		Long throwErrorId = 3L;
		Long contentMissesId = 3L;
		
		Reviewable reviewable = new Reviewable();
		reviewable.setId(1L);
		Course course = new Course();
		course.setId(1L);
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
		Long courseId = 1L;
		Content content = new Content();
		content.setId(contentId);
		content.setTitle("Java");
		content.setDescription("Introduction to Java");
		content.setTeacherId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f");
		content.setImagePath(null);
		content.setEstimatedHours(2);
		//Enroll student in the course
		CourseRegistration courseReg = new CourseRegistration();
		courseReg.setId(1L);
		courseReg.setCourse(course);
		courseReg.setStudentId(studentId);
		course.setRegistrations(List.of(courseReg));
		//Add relationship between content-course-student
		CourseRegistrationContent courseRegCon = new CourseRegistrationContent();
		courseRegCon.setId(1L);
		courseRegCon.setContent(content);
		courseRegCon.setCourseRegistration(courseReg);
		courseRegCon.setCompleted(false);
		courseReg.setCourseRegistrationContents(List.of(courseRegCon));
		ContentCompletedDTO completeDto = new ContentCompletedDTO();
		completeDto.setContentId(content.getId());
		completeDto.setCourseId(courseId);
		completeDto.setId(1L);
		completeDto.setStudentId(studentId);
		completeDto.setCompleted(true);
		
		//Mock calls
		Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
		Mockito.doThrow(new RuntimeException("Fake Exception")).when(courseRepository).findById(throwErrorId);
		Mockito.when(courseRegistrationRepository.findByCourseAndStudentId(course, studentId)).thenReturn(Optional.of(courseReg));
		Mockito.when(courseRegistrationRepository.findByCourseAndStudentId(course, studentMissesId)).thenReturn(Optional.ofNullable(null));
		Mockito.when(courseRegConRepository.findByCourseRegistrationAndContent(courseReg, content)).thenReturn(courseRegCon);
		Mockito.when(courseRegConRepository.save(Mockito.any(CourseRegistrationContent.class))).thenReturn(courseRegCon);
		Mockito.when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));
		
		//When
		ResponseEntity<?> completeContentResp = courseRegConService.complete(completeDto);
		//Then
		assertEquals(HttpStatus.CREATED, completeContentResp.getStatusCode());
		assertEquals("\"Contenido actualizado con éxito\"", completeContentResp.getBody());
		//When
		completeDto.setCourseId(courseNotFoundId);
		completeContentResp = courseRegConService.complete(completeDto);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, completeContentResp.getStatusCode());
		assertEquals("\"No se encontró el curso solicitado. Por favor, inténtelo de nuevo.\"", completeContentResp.getBody());
		//When
		completeDto.setCourseId(courseId);
		completeDto.setContentId(contentNotFoundId);
		completeContentResp = courseRegConService.complete(completeDto);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, completeContentResp.getStatusCode());
		assertEquals("\"No se encontró el contenido solicitado. Por favor, inténtelo de nuevo.\"", completeContentResp.getBody());
		//When
		completeDto.setContentId(contentId);
		completeDto.setStudentId(studentMissesId);
		completeContentResp = courseRegConService.complete(completeDto);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, completeContentResp.getStatusCode());
		assertEquals("\"Parece que no está registrado en el curso solicitado. De no ser así, por favor, inténtelo de nuevo.\"", completeContentResp.getBody());
		//When
		completeDto.setStudentId(studentId);
		completeDto.setCourseId(throwErrorId);
		completeContentResp = courseRegConService.complete(completeDto);
		//Then
		 assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, completeContentResp.getStatusCode());
		 assertEquals("\"Lo sentimos, algo salió mal. Por favor, inéntelo de nuevo.\"", completeContentResp.getBody());
	}
}
