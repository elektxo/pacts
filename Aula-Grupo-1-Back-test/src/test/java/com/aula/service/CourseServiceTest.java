package com.aula.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.aula.dto.CourseDTO;
import com.aula.dto.StudentCourseContentDTO;
import com.aula.dto.StudentCourseDTO;
import com.aula.dto.converter.BaseConverter;
import com.aula.dto.converter.impl.StudentCourseConverter;
import com.aula.entity.Content;
import com.aula.entity.Course;
import com.aula.entity.CourseContent;
import com.aula.entity.CourseRegistration;
import com.aula.entity.CourseRegistrationContent;
import com.aula.entity.Reviewable;
import com.aula.repository.BaseRepository;
import com.aula.repository.ContentRepository;
import com.aula.repository.CourseContentRepository;
import com.aula.repository.CourseRegistrationContentRepository;
import com.aula.repository.CourseRegistrationRepository;
import com.aula.repository.CourseRepository;
import com.aula.repository.ReviewableRepository;
import com.aula.repository.UserRepository;
import com.aula.service.impl.CourseServiceImpl;
import com.aula.service.impl.EmailServiceImpl;
import com.aula.service.impl.KeycloakServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CourseServiceTest {

	@InjectMocks
	private CourseServiceImpl courseService;

	@Mock
	private ContentRepository contentRepository;
	
	@Mock
	private FileStorageService fileStorageService;
	
	@Mock
	private CourseRegistrationRepository courseRegistrationRepository;
	
	@Mock
	private ReviewableRepository reviewableRepository;
	
	@Mock
	private CourseRepository courseRepository;
	
	@Mock
	private CourseContentRepository courseConRepository;
	
	@Mock
	private CourseRegistrationContentRepository courseRegConRepository;
	
	@Mock
	private EmailServiceImpl emailService;
	
	@Mock
	private KeycloakServiceImpl keycloackService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private StudentCourseConverter studentCourseConverter;
	
	@Mock BaseRepository<Course, Long> baseRepository;
	
	@Mock BaseConverter<CourseDTO, Course> baseConverter;
	
	private MockMultipartFile image;
	private Reviewable reviewable;
	private Course course;
	private Content content;
	private CourseContent courseContent;
	private CourseDTO courseDto;
	private CourseRegistrationContent courseRegCon;
	private CourseRegistration courseReg;
	
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
		reviewable.setId(1L);
		
		course = new Course();
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
		
		content = new Content();
		content.setId(1L);
		content.setTitle("Java");
		content.setDescription("Introduction to Java");
		content.setTeacherId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f");
		content.setImagePath(null);
		content.setEstimatedHours(2);
	}
	
	@Test
	void testSave() {
		//Given
		
		//Mock calls
		Mockito.when(reviewableRepository.save(any(Reviewable.class))).thenReturn(reviewable);
		Mockito.when(courseRepository.save(any(Course.class))).thenReturn(course);
		Mockito.when(baseConverter.entityToDTO(any(Course.class))).thenReturn(courseDto);
		
		//When
		ResponseEntity<?> saveResponse = courseService.save(courseDto, image);
		//Then
		assertEquals(HttpStatus.CREATED, saveResponse.getStatusCode());
		assertEquals(courseDto, saveResponse.getBody());
		//When
		courseDto = null;
		saveResponse = courseService.save(courseDto, image);
		//Then
		assertEquals(HttpStatus.BAD_REQUEST, saveResponse.getStatusCode());
		assertEquals("\"Bad request. Please, refer to the API documentation.\"", saveResponse.getBody());
		
	}
	
	@Test
	void testAddContent_ContentAlreadyExists() {
		//Given
		Long courseId = 1L;
		Long contentId = 1L;
		
		//First content to add to the course
		courseContent = new CourseContent();
		courseContent.setContent(content);
		courseContent.setCourse(course);
		courseContent.setOrderInCourse(0);
		List<CourseContent> contents = new ArrayList<>();
		contents.add(courseContent);
		course.setCourseContents(contents);
	
		Course updatedCourse = new Course();
		updatedCourse.setId(course.getId());
		updatedCourse.setTitle(course.getTitle());
		updatedCourse.setDescription(course.getDescription());
		updatedCourse.setPrice(course.getPrice());
		updatedCourse.setFree(course.getFree());
		updatedCourse.setTeacherId(course.getTeacherId());
		updatedCourse.setImagePath(course.getImagePath());
		updatedCourse.setReviewable(course.getReviewable());
		updatedCourse.setCourseContents(List.of(courseContent));
		
		//Enroll student in the course
		courseReg = new CourseRegistration();
		courseReg.setId(1L);
		courseReg.setCourse(course);
		courseReg.setStudentId("c73644f1-b3d1-4443-b1ff-5b289f7e8fef");
		course.setRegistrations(List.of(courseReg));
		
		//Add relationship between content-course-student
		courseRegCon = new CourseRegistrationContent();
		courseRegCon.setId(1L);
		courseRegCon.setContent(content);
		courseRegCon.setCourseRegistration(courseReg);
		courseRegCon.setCompleted(false);
		courseReg.setCourseRegistrationContents(List.of(courseRegCon));
		
		//Mock calls
		Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
		Mockito.when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));
		Mockito.when(courseConRepository.save(any(CourseContent.class))).thenReturn(courseContent);
		Mockito.when(courseRegConRepository.save(any(CourseRegistrationContent.class))).thenReturn(courseRegCon);
		
		//When
		ResponseEntity<?> addContentResponse = courseService.addContent(courseId, contentId);
		//Then
		assertEquals(HttpStatus.BAD_REQUEST, addContentResponse.getStatusCode());
		assertEquals("\"Content is already part of the course\"", addContentResponse.getBody());
	}
	
	@Test
	void testAddContent_FirstContent() {
		//Given
		Long courseId = 1L;
		Long contentId = 1L;
		
		//First content to add to the course
		courseContent = new CourseContent();
		courseContent.setContent(content);
		courseContent.setCourse(course);
		courseContent.setOrderInCourse(0);
		List<CourseContent> contents = new ArrayList<>();
		contents.add(courseContent);
	
		Course updatedCourse = new Course();
		updatedCourse.setId(course.getId());
		updatedCourse.setTitle(course.getTitle());
		updatedCourse.setDescription(course.getDescription());
		updatedCourse.setPrice(course.getPrice());
		updatedCourse.setFree(course.getFree());
		updatedCourse.setTeacherId(course.getTeacherId());
		updatedCourse.setImagePath(course.getImagePath());
		updatedCourse.setReviewable(course.getReviewable());
		updatedCourse.setCourseContents(List.of(courseContent));
		
		//Enroll student in the course
		courseReg = new CourseRegistration();
		courseReg.setId(1L);
		courseReg.setCourse(course);
		courseReg.setStudentId("c73644f1-b3d1-4443-b1ff-5b289f7e8fef");
		course.setRegistrations(List.of(courseReg));
		
		//Add relationship between content-course-student
		courseRegCon = new CourseRegistrationContent();
		courseRegCon.setId(1L);
		courseRegCon.setContent(content);
		courseRegCon.setCourseRegistration(courseReg);
		courseRegCon.setCompleted(false);
		courseReg.setCourseRegistrationContents(List.of(courseRegCon));
		
		//Mock calls
		Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
		Mockito.when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));
		Mockito.when(courseConRepository.save(any(CourseContent.class))).thenReturn(courseContent);
		Mockito.when(courseRegConRepository.save(any(CourseRegistrationContent.class))).thenReturn(courseRegCon);
		
		//When
		ResponseEntity<?> addContentResponse = courseService.addContent(courseId, contentId);
		//Then
		assertEquals(HttpStatus.CREATED, addContentResponse.getStatusCode());
		assertEquals("\"Content added successfully\"", addContentResponse.getBody());
	}
	
	@Test
	void testAddContent_NotFirstContent() {
		//Given
		Long courseId = 1L;
		Long newContentId = 2L;
		Long notFoundId = 3L;
		
		//First content to add to the course
		courseContent = new CourseContent();
		courseContent.setContent(content);
		courseContent.setCourse(course);
		courseContent.setOrderInCourse(0);
		List<CourseContent> contents = new ArrayList<>();
		contents.add(courseContent);
		course.setCourseContents(contents);
	
		//Extra content to test ordering
		Content content1 = new Content();
		content1.setId(2L);
		content1.setTitle("Python");
		content1.setDescription("Introduction to Python");
		content1.setTeacherId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f");
		content1.setImagePath(null);
		content1.setEstimatedHours(2);
		CourseContent courseContent1 = new CourseContent();
		courseContent1.setContent(content1);
		courseContent1.setCourse(course);
		courseContent1.setOrderInCourse(1);
		
		Course updatedCourse = new Course();
		updatedCourse.setId(course.getId());
		updatedCourse.setTitle(course.getTitle());
		updatedCourse.setDescription(course.getDescription());
		updatedCourse.setPrice(course.getPrice());
		updatedCourse.setFree(course.getFree());
		updatedCourse.setTeacherId(course.getTeacherId());
		updatedCourse.setImagePath(course.getImagePath());
		updatedCourse.setReviewable(course.getReviewable());
		updatedCourse.setCourseContents(List.of(courseContent, courseContent1));
		
		//Enroll student in the course
		courseReg = new CourseRegistration();
		courseReg.setId(1L);
		courseReg.setCourse(course);
		courseReg.setStudentId("c73644f1-b3d1-4443-b1ff-5b289f7e8fef");
		course.setRegistrations(List.of(courseReg));
		
		//Add relationship between content-course-student
		courseRegCon = new CourseRegistrationContent();
		courseRegCon.setId(1L);
		courseRegCon.setContent(content);
		courseRegCon.setCourseRegistration(courseReg);
		courseRegCon.setCompleted(false);
		courseReg.setCourseRegistrationContents(List.of(courseRegCon));
		CourseRegistrationContent courseRegCon1 = new CourseRegistrationContent();
		courseRegCon1.setId(2L);
		courseRegCon1.setContent(content1);
		courseRegCon1.setCourseRegistration(courseReg);
		courseRegCon1.setCompleted(false);
		courseReg.setCourseRegistrationContents(List.of(courseRegCon, courseRegCon1));
		
		//Mock calls
		Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
		Mockito.when(contentRepository.findById(newContentId)).thenReturn(Optional.of(content1));
		Mockito.when(courseRepository.findById(notFoundId)).thenReturn(Optional.ofNullable(null));
		Mockito.when(courseConRepository.save(any(CourseContent.class))).thenReturn(courseContent);
		Mockito.when(courseRegConRepository.save(any(CourseRegistrationContent.class))).thenReturn(courseRegCon);
		
		//When
		ResponseEntity<?> addContentResponse = courseService.addContent(courseId, newContentId);
		//Then
		assertEquals(HttpStatus.CREATED, addContentResponse.getStatusCode());
		assertEquals("\"Content added successfully\"", addContentResponse.getBody());
		//When
		addContentResponse = courseService.addContent(notFoundId, notFoundId);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, addContentResponse.getStatusCode());
		assertEquals("\"There are not such content or course registered\"", addContentResponse.getBody());
	}
	
	@Test
	void testRemoveContent() {
		//Given
		Long courseId = (long) 1;
		Long contentId = (long) 1;
			
		//Add content to the course
		courseContent = new CourseContent();
		courseContent.setId((long)1);
		courseContent.setContent(content);
		courseContent.setCourse(course);
		courseContent.setOrderInCourse(0);
		course.setCourseContents(List.of(courseContent));
		
		//Enroll student in the course
		courseReg = new CourseRegistration();
		courseReg.setId((long)1);
		courseReg.setCourseRegistrationContents(new ArrayList<>());
		courseReg.setCourse(course);
		courseReg.setStudentId("c73644f1-b3d1-4443-b1ff-5b289f7e8fef");
		
		//Add relationship between content-course-student
		courseRegCon = new CourseRegistrationContent();
		courseRegCon.setId((long)1);
		courseRegCon.setContent(content);
		courseRegCon.setCourseRegistration(courseReg);
		courseRegCon.setCompleted(false);
		courseReg.setCourseRegistrationContents(List.of(courseRegCon));
		
		//Add registration to the course
		course.setRegistrations(List.of(courseReg));
		
		Course updatedCourse = new Course();
		updatedCourse.setId(course.getId());
		updatedCourse.setTitle(course.getTitle());
		updatedCourse.setDescription(course.getDescription());
		updatedCourse.setPrice(course.getPrice());
		updatedCourse.setFree(course.getFree());
		updatedCourse.setTeacherId(course.getTeacherId());
		updatedCourse.setImagePath(course.getImagePath());
		updatedCourse.setReviewable(course.getReviewable());
		updatedCourse.setCourseContents(new ArrayList<>());
		
		//Mock calls
		Mockito.when(courseRepository.findById((long) 1)).thenReturn(Optional.of(course));
		Mockito.when(contentRepository.findById((long) 1)).thenReturn(Optional.of(content));
		Mockito.when(contentRepository.findById((long) 2)).thenReturn(Optional.ofNullable(null));
	
		//When
		ResponseEntity<?> removeContentResponse = courseService.removeContent(courseId, contentId);
		//Then
		assertEquals(HttpStatus.CREATED, removeContentResponse.getStatusCode());
		assertEquals("\"Content removed successfully\"", removeContentResponse.getBody());
		//When
		removeContentResponse = courseService.removeContent((long) 2, (long) 2);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, removeContentResponse.getStatusCode());
		assertEquals("\"There are not such content or course registered\"", removeContentResponse.getBody());
	}
	
	@Test
	void testAddStudent() {
		//Given
		Long courseId = 1L;
		String studentId = "c73644f1-b3d1-4443-b1ff-5b289f7e8fef";
			
		//Add contents to the course
		courseContent = new CourseContent();
		courseContent.setContent(content);
		courseContent.setCourse(course);
		courseContent.setOrderInCourse(0);
		List<CourseContent> contents = new ArrayList<>();
		contents.add(courseContent);
		
		//Add student to the course
		CourseRegistration registration = new CourseRegistration();
		registration.setId(1L);
		registration.setCourse(course);
		registration.setStudentId(studentId);
		List<CourseRegistration> registrations = new ArrayList<CourseRegistration>();
		registrations.add(registration);
		
		Course updatedCourse = new Course();
		updatedCourse.setId(course.getId());
		updatedCourse.setTitle(course.getTitle());
		updatedCourse.setDescription(course.getDescription());
		updatedCourse.setPrice(course.getPrice());
		updatedCourse.setFree(course.getFree());
		updatedCourse.setTeacherId(course.getTeacherId());
		updatedCourse.setImagePath(course.getImagePath());
		updatedCourse.setReviewable(reviewable);
		updatedCourse.setCourseContents(course.getCourseContents());
		updatedCourse.setRegistrations(registrations);
		
		//The CourseRegistrationContent to be saved while enrolling
		courseRegCon = new CourseRegistrationContent();
		courseRegCon.setId((long)1);
		courseRegCon.setContent(content);
		courseRegCon.setCourseRegistration(registration);
		courseRegCon.setCompleted(false);
		
		//Mock calls
		Mockito.when(courseRepository.findById((long) 1)).thenReturn(Optional.of(course));
		Mockito.when(courseRepository.findById((long) 2)).thenReturn(Optional.ofNullable(null));
		Mockito.when(courseRepository.findById((long) 3)).thenReturn(Optional.of(updatedCourse));
		Mockito.when(courseRegistrationRepository.save(any(CourseRegistration.class))).thenReturn(registration);
		Mockito.when(courseRegConRepository.save(any(CourseRegistrationContent.class))).thenReturn(courseRegCon);
	
		//When
		ResponseEntity<?> addStudentResponse = courseService.addStudent(courseId, studentId);
		//Then
		assertEquals(HttpStatus.CREATED, addStudentResponse.getStatusCode());
		assertEquals("\"Student added successfully\"", addStudentResponse.getBody());
		//When
		addStudentResponse = courseService.addStudent((long) 2, studentId);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, addStudentResponse.getStatusCode());
		assertEquals("\"There are not such student or course registered\"", addStudentResponse.getBody());
		//When
		addStudentResponse = courseService.addStudent((long) 3, studentId);
		//Then
		assertEquals(HttpStatus.BAD_REQUEST, addStudentResponse.getStatusCode());
		assertEquals("\"Student already registered in that course\"", addStudentResponse.getBody());
	}
	
	@Test
	void testRemoveStudent() {
		//Given
		Long courseId = (long) 1;
		String studentId = "c73644f1-b3d1-4443-b1ff-5b289f7e8fef";
		String fakeStudentId = "fakeId";
		
		//Add student to the course
		CourseRegistration registration = new CourseRegistration();
		registration.setId((long)1);
		registration.setCourse(course);
		registration.setStudentId(studentId);
		CourseRegistration fakeRegistration = new CourseRegistration();
		fakeRegistration.setId((long)2);
		fakeRegistration.setCourse(course);
		fakeRegistration.setStudentId(fakeStudentId);
		List<CourseRegistration> registrations = new ArrayList<CourseRegistration>();
		registrations.addAll(List.of(registration, fakeRegistration));
		course.setRegistrations(registrations);
		
		//Mock calls
		Mockito.when(courseRepository.findById((long) 1)).thenReturn(Optional.of(course));
		Mockito.when(courseRepository.findById((long) 2)).thenReturn(Optional.ofNullable(null));
		Mockito.when(courseRegistrationRepository.findByCourseAndStudentId(course, studentId)).thenReturn(Optional.of(registration));
		Mockito.when(courseRegistrationRepository.findByCourseAndStudentId(course, fakeStudentId)).thenReturn(Optional.ofNullable(null));
		
	
		//When
		ResponseEntity<?> removeStudentResponse = courseService.removeStudent(courseId, studentId);
		//Then
		assertEquals(HttpStatus.CREATED, removeStudentResponse.getStatusCode());
		assertEquals("\"Student removed successfully\"", removeStudentResponse.getBody());
		//When
		removeStudentResponse = courseService.removeStudent((long) 2, studentId);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, removeStudentResponse.getStatusCode());
		assertEquals("\"There are not such student or course registered\"", removeStudentResponse.getBody());
		//When
		removeStudentResponse = courseService.removeStudent(1L, fakeStudentId);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, removeStudentResponse.getStatusCode());
		assertEquals("\"There are not such student or course registered\"", removeStudentResponse.getBody());
	}
	
	@Test
	void testGetStudents() {
		//Given
		Long courseId = (long) 1;
		String studentId = "c73644f1-b3d1-4443-b1ff-5b289f7e8fef";
		String fakeStudentId = "fakeId";

		//Enroll student in the course
		CourseRegistration registration = new CourseRegistration();
		registration.setId((long)1);
		registration.setCourse(course);
		registration.setStudentId(studentId);
		CourseRegistration fakeRegistration = new CourseRegistration();
		fakeRegistration.setId((long)2);
		fakeRegistration.setCourse(course);
		fakeRegistration.setStudentId(fakeStudentId);
		List<CourseRegistration> registrations = new ArrayList<CourseRegistration>();
		registrations.addAll(List.of(registration, fakeRegistration));
		course.setRegistrations(registrations);
		
		//Users returned
		UserRepresentation user = new UserRepresentation();
		user.setId(studentId);
		List<UserRepresentation> users = new ArrayList<UserRepresentation>();
		users.add(user);
		
		//Mock calls
		Mockito.when(courseRepository.findById((long) 1)).thenReturn(Optional.of(course));
		Mockito.when(courseRepository.findById((long) 2)).thenReturn(Optional.ofNullable(null));
		Mockito.when(userRepository.findById(studentId)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.findById(fakeStudentId)).thenReturn(Optional.ofNullable(null));
	
		//When
		ResponseEntity<?> getStudentsResponse = courseService.getStudents(courseId);
		//Then
		assertEquals(HttpStatus.OK, getStudentsResponse.getStatusCode());
		assertEquals(users, getStudentsResponse.getBody());
		//When
		getStudentsResponse = courseService.getStudents((long) 2);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, getStudentsResponse.getStatusCode());
		assertEquals("\"There are not such course registered\"", getStudentsResponse.getBody());
		//When
		getStudentsResponse = courseService.getStudents(courseId);
	}
	
	@Test
	void testSearch() {
		//Given
		String filterMatch = "AV";
		String filterMiss = "ZZZZ";
		
		//Mock calls
		Mockito.when(courseRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(filterMatch, filterMatch)).thenReturn(List.of(course));
		Mockito.when(courseRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(filterMiss, filterMiss)).thenReturn(new ArrayList<Course>());
		Mockito.when(baseConverter.entityToDTO(course)).thenReturn(courseDto);
	
		//When
		ResponseEntity<?> searchResponse = courseService.search(filterMatch);
		//Then
		assertEquals(HttpStatus.OK, searchResponse.getStatusCode());
		assertEquals(List.of(courseDto), searchResponse.getBody());
		//When
		searchResponse = courseService.search(filterMiss);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, searchResponse.getStatusCode());
		assertEquals("\"There are not such course registered\"", searchResponse.getBody());
	}
	
	@Test
	void testFindByFree() {
		//Given
		Boolean isFree = true;
		Boolean isNotFree = false;
		
		List<Course> courses = new ArrayList<Course>();
		courses.add(course);
		
		List<CourseDTO> dtos = new ArrayList<CourseDTO>();
		dtos.add(courseDto);
		
		//Mock calls
		Mockito.when(courseRepository.findByFree(isNotFree)).thenReturn(courses);
		Mockito.when(courseRepository.findByFree(isFree)).thenReturn(new ArrayList<Course>());
		Mockito.when(baseConverter.entityToDTO(course)).thenReturn(courseDto);
	
		//When
		ResponseEntity<?> findByFreeResponse = courseService.findByFree(isNotFree);
		//Then
		assertEquals(HttpStatus.OK, findByFreeResponse.getStatusCode());
		assertEquals(dtos, findByFreeResponse.getBody());
		//When
		findByFreeResponse = courseService.findByFree(isFree);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, findByFreeResponse.getStatusCode());
		assertEquals("\"There are not such course registered\"", findByFreeResponse.getBody());
	}
	
	@Test
	void testGetAllByStudentId() {
		//Given
		String studentId = "c73644f1-b3d1-4443-b1ff-5b289f7e8fef";
		courseReg = new CourseRegistration();
		courseReg.setId((long)1);
		courseReg.setCourse(course);
		courseReg.setStudentId(studentId);
		List<CourseRegistration> courseRegistrations = List.of(courseReg);
		
		StudentCourseContentDTO studentCourConDto = new StudentCourseContentDTO();
		studentCourConDto.setId(content.getId());
		studentCourConDto.setTitle(content.getTitle());
		studentCourConDto.setDescription(content.getDescription());
		studentCourConDto.setEstimatedHours(content.getEstimatedHours());
		studentCourConDto.setImagePath(content.getImagePath());
		studentCourConDto.setOrderInCourse(0);
		studentCourConDto.setCompleted(false);
		
		StudentCourseDTO studentCourseDTO = new StudentCourseDTO();
		studentCourseDTO.setId(course.getId());
		studentCourseDTO.setTitle(course.getTitle());
		studentCourseDTO.setDescription(course.getDescription());
		studentCourseDTO.setIdReviewable(course.getReviewable().getId());
		studentCourseDTO.setImagePath(course.getImagePath());
		studentCourseDTO.setFree(course.getFree());
		studentCourseDTO.setPrice(course.getPrice());
		studentCourseDTO.setContents(List.of(studentCourConDto));
		
		List<StudentCourseDTO> studentCourseDTOs = List.of(studentCourseDTO);
		
		//Mock calls
		Mockito.when(courseRegistrationRepository.findByStudentId(studentId)).thenReturn(courseRegistrations);
		Mockito.when(courseRegistrationRepository.findByStudentId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f")).thenReturn(new ArrayList<>());
		Mockito.when(studentCourseConverter.entityToDTO(courseReg)).thenReturn(studentCourseDTO);
		
		//When
		ResponseEntity<?> getAllByStudentIdResp = courseService.getAllByStudentId(studentId);
		//Then
		assertEquals(HttpStatus.OK, getAllByStudentIdResp.getStatusCode());
		assertEquals(studentCourseDTOs, getAllByStudentIdResp.getBody());
		//When
		getAllByStudentIdResp = courseService.getAllByStudentId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f");		
		//Then
		assertEquals(HttpStatus.NOT_FOUND, getAllByStudentIdResp.getStatusCode());
		assertEquals("\"There are not such course registered\"", getAllByStudentIdResp.getBody());
	}
	
	@Test
	void testFindTeacherCourses() {
		//Given
		String teacherId = "id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f";
		
		List<Course> teacherCourses = List.of(course);
		List<CourseDTO> teacherCoursesDto = List.of(courseDto);
		
		//Mock calls
		Mockito.when(courseRepository.findByTeacherId(teacherId)).thenReturn(teacherCourses);
		Mockito.when(courseRepository.findByTeacherId("c73644f1-b3d1-4443-b1ff-5b289f7e8fef")).thenReturn(new ArrayList<>());
		Mockito.when(baseConverter.entityToDTO(any(Course.class))).thenReturn(courseDto);
		
		//When
		ResponseEntity<?> findTeacherCoursesResp = courseService.findTeacherCourses(teacherId);
		//Then
		assertEquals(HttpStatus.OK, findTeacherCoursesResp.getStatusCode());
		assertEquals(teacherCoursesDto, findTeacherCoursesResp.getBody());
		//When
		findTeacherCoursesResp = courseService.findTeacherCourses("c73644f1-b3d1-4443-b1ff-5b289f7e8fef");
		//Then
		assertEquals(HttpStatus.NOT_FOUND, findTeacherCoursesResp.getStatusCode());
		assertEquals("\"There are not such course registered\"", findTeacherCoursesResp.getBody());
	}
	
	@Test
	void testFindTeacherCoursesThrowsException() {
	//Given
		String teacherId = "throws";
	//Mock calls
		Mockito.doThrow(new RuntimeException("Fake Exception")).when(courseRepository).findByTeacherId(teacherId);
	//When
		ResponseEntity<?> findTeacherCoursesResp = courseService.findTeacherCourses(teacherId);
	//Then
		 assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, findTeacherCoursesResp.getStatusCode());
		 assertEquals("\"Error attempting to find courses for theacher with id: " + teacherId + ". Fake Exception\"", findTeacherCoursesResp.getBody());
	}
	
	@Test
	void testDelete() {
	//Given
		Long courseId = 1L;
		String fakeStudentId = "fakeId";
		String studentId = "c73644f1-b3d1-4443-b1ff-5b289f7e8fef";
		UserRepresentation user = new UserRepresentation();
		user.setId(studentId);
		
		//Add content to the course
		courseContent = new CourseContent();
		courseContent.setContent(content);
		courseContent.setCourse(course);
		courseContent.setOrderInCourse(0);
		List<CourseContent> contents = new ArrayList<>();
		contents.add(courseContent);
		course.setCourseContents(contents);
		
		//Enroll student in the course
		List<CourseRegistration> courseRegistrations = new ArrayList<>();
		courseReg = new CourseRegistration();
		courseReg.setId(1L);
		courseReg.setCourse(course);
		courseReg.setStudentId("c73644f1-b3d1-4443-b1ff-5b289f7e8fef");
		CourseRegistration fakeRegistration = new CourseRegistration();
		fakeRegistration.setId((long)2);
		fakeRegistration.setCourse(course);
		fakeRegistration.setStudentId(fakeStudentId);
		courseRegistrations.addAll(List.of(courseReg, fakeRegistration));
		course.setRegistrations(courseRegistrations);
		
		//Add relationship between content-course-student
		List<CourseRegistrationContent> courseRegContents = new ArrayList<>();
		courseRegCon = new CourseRegistrationContent();
		courseRegCon.setId(1L);
		courseRegCon.setContent(content);
		courseRegCon.setCourseRegistration(courseReg);
		courseRegCon.setCompleted(false);
		courseRegContents.add(courseRegCon);
		courseReg.setCourseRegistrationContents(courseRegContents);
		
		
		//Mock calls
		Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
		Mockito.when(userRepository.findById(studentId)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.findById(fakeStudentId)).thenReturn(Optional.ofNullable(null));
		Mockito.doNothing().when(courseRepository).delete(any(Course.class));
		
		//When
		ResponseEntity<?> deleteCourseResp = courseService.delete(courseId);
		//Then
		assertEquals(HttpStatus.NO_CONTENT, deleteCourseResp.getStatusCode());
		assertEquals(null, deleteCourseResp.getBody());
		//When
		deleteCourseResp = courseService.delete(2L);
		//Then
		assertEquals(HttpStatus.NOT_FOUND, deleteCourseResp.getStatusCode());
	}
	
	@Test
	void testDeleteThrowsException() {
		//Given
		Long courseId = 2L;
		//Mock calls
		Mockito.doThrow(new RuntimeException("Fake Exception")).when(courseRepository).findById(anyLong());
		//When
		ResponseEntity<?> deleteCourseResp = courseService.delete(courseId);
		//Then
		 assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, deleteCourseResp.getStatusCode());
		 assertEquals("\"Error attempting to delete Course with id: " + courseId + " Fake Exception\"", deleteCourseResp.getBody());
	}
					
}
