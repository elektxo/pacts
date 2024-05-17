package com.aula.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.aula.dto.ContentCompletedDTO;
import com.aula.dto.converter.BaseConverter;
import com.aula.dto.converter.impl.CourseRegistrationContentConverter;
import com.aula.entity.Content;
import com.aula.entity.Course;
import com.aula.entity.CourseRegistration;
import com.aula.entity.CourseRegistrationContent;
import com.aula.repository.BaseRepository;
import com.aula.repository.ContentRepository;
import com.aula.repository.CourseRegistrationContentRepository;
import com.aula.repository.CourseRegistrationRepository;
import com.aula.repository.CourseRepository;

@Service
public class CourseRegistrationContentService 
	extends BaseServiceImpl<ContentCompletedDTO, Long, CourseRegistrationContent, CourseRegistrationContentConverter> {

	private final CourseRegistrationContentRepository courseRegConRepository;
	private final CourseRegistrationRepository courseRegistrationRepository;
	private final CourseRepository courseRepository;
	private final ContentRepository contentRepository;
	private static final Logger LOGGER = LoggerFactory.getLogger(CourseRegistrationContentService.class);
	
	public CourseRegistrationContentService(
			BaseRepository<CourseRegistrationContent, Long> baseRepository,
			BaseConverter<ContentCompletedDTO, CourseRegistrationContent> baseConverter,
			CourseRegistrationContentRepository courseRegConRepository,
			CourseRegistrationRepository courseRegistrationRepository,
			CourseRepository courseRepository,
			ContentRepository contentRepository) {
		
		super(baseRepository, baseConverter);
		
		this.courseRegConRepository = courseRegConRepository;
		this.courseRegistrationRepository = courseRegistrationRepository;
		this.courseRepository = courseRepository;
		this.contentRepository = contentRepository;
	}
	
	public ResponseEntity<?> complete(ContentCompletedDTO contentCompleteDTO) {
		try {
			Optional<Course> optCourse = courseRepository.findById(contentCompleteDTO.getCourseId());
			if (optCourse.isPresent()) {
				Optional<CourseRegistration> optCourseReg = courseRegistrationRepository.findByCourseAndStudentId(optCourse.get(), contentCompleteDTO.getStudentId());
				if (optCourseReg.isPresent()) {
					Optional<Content> optContent = contentRepository.findById(contentCompleteDTO.getContentId());
					if (optContent.isPresent()) {
						CourseRegistrationContent courseRegCon = courseRegConRepository.findByCourseRegistrationAndContent(optCourseReg.get(), optContent.get());
						courseRegCon.setCompleted(contentCompleteDTO.getCompleted());
						courseRegConRepository.save(courseRegCon);
						return ResponseEntity
								.status(HttpStatus.CREATED)
								.body("\"Contenido actualizado con éxito\"");
					}
					LOGGER.warn(
							"Content with id: " + contentCompleteDTO.getContentId() + 
							" not found while trying to complete it for the user with id: " + contentCompleteDTO.getStudentId()
					);
					return ResponseEntity
							.status(HttpStatus.NOT_FOUND)
							.body("\"No se encontró el curso solicitado. Por favor, inténtelo de nuevo.\"");
				}
				LOGGER.warn(
						"CourseRegistration for the course with id: " + contentCompleteDTO.getCourseId() + 
						" not found while trying to complete content with id: " + contentCompleteDTO.getContentId() + 
						" for the user with id: " + contentCompleteDTO.getStudentId()
				);
				return ResponseEntity
						.status(HttpStatus.NOT_FOUND)
						.body("\"Parece que no está registrado en el curso solicitado. De no ser así, por favor, inténtelo de nuevo.\"");
			} 
			LOGGER.warn(
				"Course with id: " + contentCompleteDTO.getCourseId() + 
				" not found while trying to complete content with id: " + contentCompleteDTO.getContentId() + 
				" for the user with id: " + contentCompleteDTO.getStudentId()
			);
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body("\"No se encontró el curso solicitado. Por favor, inténtelo de nuevo.\"");
		} catch (Exception e) {
			LOGGER.error("Error intentando completar el contenido: " + e.getMessage());
			return ResponseEntity
				  .status(HttpStatus.INTERNAL_SERVER_ERROR)
				  .body("\"Lo sentimos, algo salió mal. Por favor, inéntelo de nuevo.\"");
		}
	}
}
