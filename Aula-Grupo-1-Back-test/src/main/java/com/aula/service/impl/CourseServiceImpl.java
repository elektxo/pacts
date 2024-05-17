package com.aula.service.impl;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.aula.repository.*;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.aula.dto.CourseDTO;
import com.aula.dto.StudentCourseDTO;
import com.aula.dto.converter.BaseConverter;
import com.aula.dto.converter.impl.CourseConverter;
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
import com.aula.service.FileStorageService;

import jakarta.transaction.Transactional;

import org.springframework.web.multipart.MultipartFile;

@Service
public class CourseServiceImpl extends BaseServiceImpl<CourseDTO, Long, Course, CourseConverter> {
		
	private final ContentRepository contentRepository;
	private final StudentCourseConverter studentCourseConverter;
	private final FileStorageService fileStorageService;
	private final CourseRegistrationRepository courseRegistrationRepository;
	private final ReviewableRepository reviewableRepository;
	private final CourseRepository courseRepository;
	private final CourseRegistrationContentRepository courseRegConRepository;
	private final UserRepository userRepository;
	private final CourseContentRepository  courseContentRepository;
	private final EmailServiceImpl emailService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CourseServiceImpl.class);
	
	public CourseServiceImpl(
			BaseRepository<Course, Long> baseRepository, 
			BaseConverter<CourseDTO, Course> baseConverter,
			ContentRepository contentRepository,
			FileStorageService fileStorageService,
			CourseRegistrationRepository courseRegistrationRepository,
			ReviewableRepository reviewableRepository,
			CourseRepository courseRepository,
			CourseContentRepository  courseContentRepository,
			CourseRegistrationContentRepository courseRegistrationContentRepository,
			StudentCourseConverter studentCourseConverter,
			UserRepository userRepository,
			EmailServiceImpl emailService) {
		
		super(baseRepository, baseConverter);
		this.contentRepository = contentRepository;
		this.fileStorageService = fileStorageService;
		this.courseRegistrationRepository = courseRegistrationRepository;
		this.reviewableRepository = reviewableRepository;
		this.courseRepository = courseRepository;
		this.courseContentRepository = courseContentRepository;
		this.courseRegConRepository = courseRegistrationContentRepository;
		this.studentCourseConverter = studentCourseConverter;
		this.userRepository = userRepository;
		this.emailService = emailService;
    }

	public ResponseEntity<?> save(CourseDTO dto, MultipartFile image) {
		try {
				Course course = new Course();
				course.setTitle(dto.getTitle());
				course.setDescription(dto.getDescription());
				course.setPrice(dto.getPrice());
				course.setFree(dto.getFree());
				course.setTeacherId(dto.getTeacherId());
				course.setReviewable(reviewableRepository.save(new Reviewable()));
				course.setCourseContents(new ArrayList<>());
				if (image != null) {
					String filename = fileStorageService.store(image, "course");
					course.setImagePath(filename);
				}
				course.setComments(new ArrayList<>());
				course = courseRepository.save(course);
				LOGGER.info("New course created: " + course.getId());
				return ResponseEntity
						.status(HttpStatus.CREATED)
						.body(baseConverter.entityToDTO(course));
		  } catch (Exception e) {
			  LOGGER.error("Error creating course: " + e.getMessage());
			  return ResponseEntity
					  .status(HttpStatus.BAD_REQUEST)
					  .body("\"Bad request. Please, refer to the API documentation.\"");
		}		
	}
	
	public ResponseEntity<?> addContent(Long courseId, Long contentId) {
		Optional<Course> optCourse = courseRepository.findById(courseId);
		if (optCourse.isPresent()) {
			Optional<Content> optContent = contentRepository.findById(contentId);
			if (optContent.isPresent()) {
				Content content = optContent.get();
				Course course = optCourse.get();
				List<CourseContent> courseContents = course.getCourseContents();
				if (!course.getContents().contains(content)) {
					var courseContentsSize = courseContents.size();
					CourseContent courseAddition = new CourseContent();
					courseAddition.setContent(content);
					courseAddition.setCourse(course);
					if (courseContentsSize > 0) {
						courseAddition.setOrderInCourse(courseContents.get(courseContents.size() - 1).getOrderInCourse() + 1);
					} else {
						courseAddition.setOrderInCourse(0);
					}
					courseContentRepository.save(courseAddition);
					LOGGER.info("Added content with id: " + contentId + " to course with id: " + courseId);
					for (CourseRegistration courseReg : course.getRegistrations()) {
						CourseRegistrationContent courseRegCon = new CourseRegistrationContent();
						courseRegCon.setCourseRegistration(courseReg);
						courseRegCon.setContent(content);
						courseRegCon.setCompleted(false);
						courseRegCon = courseRegConRepository.save(courseRegCon);
						LOGGER.info("Added CourseRegistrationContetn with id: " + courseRegCon.getId() + " to track progress of student with id: " + courseReg.getStudentId());
					}
					return ResponseEntity
							.status(HttpStatus.CREATED)
							.body("\"Content added successfully\"");
				} else {
					LOGGER.warn("Attempt to add content with id: " + contentId + " to course with id: " + courseId + " when content was already in course");
					return ResponseEntity
							.status(HttpStatus.BAD_REQUEST)
							.body("\"Content is already part of the course\"");
				}
			} else {
				LOGGER.warn("Attempt to add content with id: " + contentId + " to course with id: " + courseId + " failed because content could not be found");
			}
		} else {
			LOGGER.warn("Attempt to add content with id: " + contentId + " to course with id: " + courseId + " failed because course could not be found");
		}
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body("\"There are not such content or course registered\"");
	}
	
	public ResponseEntity<?> removeContent(Long courseId, Long contentId) {
		Optional<Content> optContent = contentRepository.findById(contentId);
		if (optContent.isPresent()) {
			Optional<Course> optCourse = courseRepository.findById(courseId);
			if (optCourse.isPresent()) {
				Content content = optContent.get();
				Course course = optCourse.get();
				CourseContent courseContent = courseContentRepository.findByContentAndCourse(content, course);
				List<CourseRegistration> courseRegistrations = course.getRegistrations();
				for (CourseRegistration courseReg : courseRegistrations) {
					CourseRegistrationContent courseRegCon = courseRegConRepository.findByCourseRegistrationAndContent(courseReg, content);
					courseRegConRepository.delete(courseRegCon);
				}
				courseContentRepository.delete(courseContent);
				LOGGER.info("Removed content with id: " + contentId + " from course with id: " + courseId);
				return ResponseEntity
						.status(HttpStatus.CREATED)
						.body("\"Content removed successfully\"");
			} else {
				LOGGER.warn("Attempt to remove content with id: " + contentId + " from course with id: " + courseId + " failed because course could not be found");
			}
		} else {
			LOGGER.warn("Attempt to remove content with id: " + contentId + " from course with id: " + courseId + " failed because course could not be found");
		}
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body("\"There are not such content or course registered\"");
	}
	
	public ResponseEntity<?> addStudent(Long courseId, String studentId) {
		Optional<Course> optCourse = courseRepository.findById(courseId);
		if (optCourse.isPresent()) {
			Course course = optCourse.get();
			List<CourseRegistration> registrations = course.getRegistrations();
			for (CourseRegistration registration : registrations) {
				if (registration.getStudentId().equals(studentId)) {
					LOGGER.warn("Attempt to add student with id: " + studentId + " to course with id: " + courseId + " when student was already registered in course");
					return ResponseEntity
							.status(HttpStatus.BAD_REQUEST)
							.body("\"Student already registered in that course\"");
				}
			}
			CourseRegistration newRegistration = new CourseRegistration();
			newRegistration.setCourse(course);
			newRegistration.setStudentId(studentId);
			courseRegistrationRepository.save(newRegistration);
			LOGGER.info("Added student with id: " + studentId + " to course with id: " + courseId);
			for (Content content : course.getContents()) {
				CourseRegistrationContent courseRegCon = new CourseRegistrationContent();
				courseRegCon.setContent(content);
				courseRegCon.setCourseRegistration(newRegistration);
				courseRegCon.setCompleted(false);
				courseRegCon = courseRegConRepository.save(courseRegCon);
				LOGGER.info("Added new CourseRegistrationContent with id: " + courseRegCon.getId() + "to track progress of student with id: " + studentId);
			}
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body("\"Student added successfully\"");
		} else {
			LOGGER.warn("Attempt to register student with id: " + studentId + " to course with id: " + courseId + " failed because course could not be found");
		}
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body("\"There are not such student or course registered\"");
	}
	
	public ResponseEntity<?> removeStudent(Long courseId, String studentId) {
		Optional<Course> optCourse = courseRepository.findById(courseId);
			if (optCourse.isPresent()) {
			Course course = optCourse.get();
			Optional<CourseRegistration> optRegistration = courseRegistrationRepository.findByCourseAndStudentId(course, studentId);
			if (optRegistration.isPresent()) {
				courseRegistrationRepository.delete(optRegistration.get());
				LOGGER.info("Removed student with id: " + studentId + " from course with id: " + courseId);
				return ResponseEntity
					.status(HttpStatus.CREATED)
					.body("\"Student removed successfully\"");
			} else {
				LOGGER.warn("Attempt to remove student with id: " + studentId + " from course with id: " + courseId + " failed because no registration was found");
			}
		} else {
			LOGGER.warn("Attempt to remove student with id: " + studentId + " from course with id: " + courseId + " failed because course could not be found");
		}
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body("\"There are not such student or course registered\"");
	}

	public ResponseEntity<?> getStudents(Long courseId) {
		Optional<Course> optCourse = courseRepository.findById(courseId);
		if (optCourse.isPresent()) {
			Course course = optCourse.get();
			List<CourseRegistration> registrations = course.getRegistrations();
			List<UserRepresentation> students = new ArrayList<>();
			for (CourseRegistration registration : registrations) {
				String studentId = registration.getStudentId();
				Optional<UserRepresentation> optUser = userRepository.findById(studentId);
				if (optUser.isPresent()) {
					students.add(optUser.get());
				} else {
					LOGGER.warn("Failed to fetch student with id: " + studentId + " when fetching students registered to course with id: " + courseId);
				}
			}
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(students);
		} else {
			LOGGER.warn("Attempt to get students registered in course with id: " + courseId + " failed because course could not be found");
		}
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body("\"There are not such course registered\"");
	}

	public ResponseEntity<?> search(String filter) {
		List<Course> courses = courseRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(filter, filter);
		if (courses.isEmpty()) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body("\"There are not such course registered\"");
		} else {
			List<CourseDTO> dtos = new ArrayList<>();
			for (Course course : courses) {
				CourseDTO dto = baseConverter.entityToDTO(course);
				dtos.add(dto);
			}
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(dtos);
		}
	}
	
	public ResponseEntity<?> findByFree(Boolean free) {
		List<Course> courses = courseRepository.findByFree(free);
		if (courses.isEmpty()) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body("\"There are not such course registered\"");
		} else {
			List<CourseDTO> dtos = new ArrayList<>();
			for (Course course : courses) {
				CourseDTO dto = baseConverter.entityToDTO(course);
				dtos.add(dto);
			}
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(dtos);
		}
	}

	public ResponseEntity<?> getAllByStudentId(String studentId) {
		List<CourseRegistration> registrations = courseRegistrationRepository.findByStudentId(studentId);
		if (registrations.isEmpty()) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body("\"There are not such course registered\"");
		} else {
			List<StudentCourseDTO> studentCourses = new ArrayList<>();
			for (CourseRegistration registration : registrations) {
				StudentCourseDTO dto = studentCourseConverter.entityToDTO(registration);
				studentCourses.add(dto);
			}
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(studentCourses);
		}
	}

	public ResponseEntity<?> getCourseByStudentId(Long courseId, String studentId) {
		Optional<Course> optCourse = courseRepository.findById(courseId);
		if (optCourse.isPresent()) {
			Course course = optCourse.get();
			Optional<CourseRegistration> optRegistration = courseRegistrationRepository.findByCourseAndStudentId(course, studentId);
			if (optRegistration.isPresent()) {
				StudentCourseDTO dto = studentCourseConverter.entityToDTO(optRegistration.get());
				return ResponseEntity
						.status(HttpStatus.OK)
						.body(dto);
			} else {
				LOGGER.warn("Attempt to get course with id: " + courseId + " for student with id: " + studentId + " failed because student is not registered in course");
			}
		} else {
			LOGGER.warn("Attempt to get course with id: " + courseId + " for student with id: " + studentId + " failed because course could not be found");
		}
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body("\"There are not such course registered\"");
	}

	public ResponseEntity<?> findTeacherCourses(String teacherId){
		try {
			List<Course> courses = courseRepository.findByTeacherId(teacherId);
			if(courses.isEmpty()){
				return ResponseEntity
						.status(HttpStatus.NOT_FOUND)
						.body("\"There are not such course registered\"");
			}else{
				List<CourseDTO> teacherCourses = new ArrayList<>();
				for(Course course : courses){
					CourseDTO dto = baseConverter.entityToDTO(course);
					teacherCourses.add(dto);
				}
				return ResponseEntity
						.status(HttpStatus.OK)
						.body(teacherCourses);
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("\"Error attempting to find courses for theacher with id: " + teacherId + ". " + e.getMessage() + "\"");
		}
	}

	public ResponseEntity<Resource> getFile(Long courseId) {
		try {
			Optional<Course> optionalCourse = courseRepository.findById(courseId);
			if(optionalCourse.isPresent()) {
				Resource file = fileStorageService.loadAsResource(optionalCourse.get().getImagePath());
				String contentType = Files.probeContentType(file.getFile().toPath());
				return ResponseEntity.ok()
						.contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
						.body(file);
			} else {
				LOGGER.warn("Failed to fetch file for course with id: " + courseId + " because course was not found");
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
	
	@Override
	@Transactional
	public ResponseEntity<?> delete(Long courseId) {
		try {
			Optional<Course> optCourse = courseRepository.findById(courseId);
			if (optCourse.isPresent()) {
				Course course = optCourse.get();
				//List of enrolled users to be notified after deletion
				List<UserRepresentation> enrolledUsers = new ArrayList<>();
				// Detach the course from all the related contents and registrations
				List<CourseContent> courseContents = course.getCourseContents();
				for(CourseContent courseContent : courseContents) {
					courseContentRepository.delete(courseContent);
					LOGGER.info("Deleted CourseContent with id " + courseContent.getId() + " while deleting Course with id: " + courseId);
				}
				List<CourseRegistration> courseRegistrations = course.getRegistrations();
	            for (CourseRegistration courseReg : courseRegistrations) {
	            	Optional<UserRepresentation> optUser = userRepository.findById(courseReg.getStudentId());
	            	if (optUser.isPresent()) {
	            		enrolledUsers.add(optUser.get());
	            		for (CourseRegistrationContent courseRegCon : courseReg.getCourseRegistrationContents()) {
		            		courseRegConRepository.delete(courseRegCon);
		            		LOGGER.info("Deleted CourseRegistrationContent with id " + courseRegCon.getId() + " while deleting Course with id: " + courseId);
		            	}
		            	courseRegistrationRepository.delete(courseReg);
		            	LOGGER.info("Deleted CourseRegistration with id " + courseReg.getId() + " while deleting Course with id: " + courseId);
	            	} else {
	            		LOGGER.warn("Unable to retrieve user data for student with id: " + courseReg.getStudentId() + ". No email will be sent.");
	            	}
	          
	            }
	    		courseRepository.delete(course);
	    		for (UserRepresentation user : enrolledUsers) {
	    			var to = user.getEmail();
	    			var subject = "Course " + course.getTitle() + " has been deleted by its teacher and is no longer available.";
	    			var text = "We inform you that the course " + course.getTitle() + " has been deleted by its teacher and therefore is no longer available. We encourage you to keep learning with our extense catalog of courses and the help of our teachers.";
	    			emailService.sendSimpleMessage(
	    					to,
	    					subject,
	    					text);	    			
    				LOGGER.error("Email sent to: " + user.getEmail() + " notifying that the course with id: " + courseId + " was deleted.");
	    		}
	    		return ResponseEntity
	    				.status(HttpStatus.NO_CONTENT)
	    				.body(null);
			} else {
            	LOGGER.warn("Course with id " + courseId + " was not found when trying to delete it");
                return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
        	LOGGER.error("Error when trying to delete Course with id " + courseId + " : " + e.getMessage());
            return ResponseEntity.internalServerError().body("\"Error attempting to delete Course with id: " + courseId + " " + e.getMessage() + "\"");
        }
	}

}
