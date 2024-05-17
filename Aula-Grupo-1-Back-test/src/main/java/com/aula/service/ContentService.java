package com.aula.service;

import com.aula.dto.RequestContentDTO;
import com.aula.dto.ResponseContentDTO;
import com.aula.dto.converter.CommentConverter;
import com.aula.entity.Content;
import com.aula.entity.Course;

import com.aula.entity.CourseContent;
import com.aula.entity.CourseRegistration;
import com.aula.entity.CourseRegistrationContent;

import com.aula.repository.ContentRepository;
import com.aula.repository.CourseContentRepository;
import com.aula.repository.CourseRegistrationContentRepository;
import com.aula.repository.CourseRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContentService {
	
    private final ContentRepository contentRepository;
    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;
    private final CourseContentRepository courseContentRepository;
    private final CourseRegistrationContentRepository courseRegConRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentService.class);
    
    public ContentService(
    		ContentRepository contentRepository, 
    		FileStorageService fileStorageService, 
    		CourseContentRepository courseContentRepository,
    		CourseRegistrationContentRepository courseRegConRepository,
    		CourseRepository courseRepository
    ) {
        this.contentRepository = contentRepository;
        this.fileStorageService = fileStorageService;
        this.courseContentRepository = courseContentRepository;
        this.courseRegConRepository = courseRegConRepository;
        this.courseRepository = courseRepository;
    }

    public ResponseEntity<List<ResponseContentDTO>> getAllContents() {
        List<ResponseContentDTO> contents = new ArrayList<>();
        for (Content content : contentRepository.findAll()) {
            contents.add(new ResponseContentDTO(content.getId(), content.getTitle(), content.getDescription(), content.getEstimatedHours(), content.getImagePath(),CommentConverter.ListEntityToListDTO(content.getComments())));
        }
        return ResponseEntity.ok(contents);
    }

    public ResponseEntity<ResponseContentDTO> getContentById(Long contentId) {
        Content content = contentRepository.findById(contentId).orElse(null);
        if (content == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ResponseContentDTO(content.getId(), content.getTitle(), content.getDescription(), content.getEstimatedHours(), content.getImagePath(),CommentConverter.ListEntityToListDTO(content.getComments())));
    }

    public ResponseEntity<String> createContent(RequestContentDTO requestContentDTO, MultipartFile image) {
        try {
            Content content = new Content();
            content.setTitle(requestContentDTO.getTitle());
            content.setDescription(requestContentDTO.getDescription());
            content.setEstimatedHours(requestContentDTO.getEstimatedHours());
            content.setTeacherId(requestContentDTO.getTeacherId());
            if (image != null) {
                String filename = fileStorageService.store(image, "content");
                content.setImagePath(filename);
            }
            content = contentRepository.save(content);
            LOGGER.info("New content created: " + content.getId());
            return ResponseEntity.ok("\"Content successfully created with id: " + content.getId() + "\"");
        } catch (Exception e) {
        	LOGGER.error("Error creating content: " + e.getMessage());
            return ResponseEntity.internalServerError().body("\"Error attempting to create content: " + e.getMessage() + "\"");
        }
    }


    public ResponseEntity<String> deleteContent(Long contentId) {
        try {
            Optional<Content> optionalContent = contentRepository.findById(contentId);
            if (optionalContent.isPresent()) {
                Content content = optionalContent.get();
                // Detach the course from all the related courses and its registrations
                List<CourseContent> courseContents = content.getCourseContents();
                if (courseContents != null) {
                	for(CourseContent courseContent : courseContents) {
                    	Course course = courseContent.getCourse();
                        courseContentRepository.delete(courseContent);
                        LOGGER.info("Deleted CourseContent with id " + courseContent.getId() + " while deleting Content with id: " + contentId);
                        
                        List<CourseRegistration> courseRegistrations = course.getRegistrations();
                        for (CourseRegistration courseReg : courseRegistrations) {
                        	CourseRegistrationContent courseRegCon = courseRegConRepository.findByCourseRegistrationAndContent(courseReg, content);
                        	courseRegConRepository.delete(courseRegCon);
                        	LOGGER.info("Deleted CourseRegistrationContent with id " + courseRegCon.getId() + " while deleting Content with id: " + contentId);
                        }
                    }
                } else {
                	LOGGER.warn("No CourseContents were found while deleting content with id " + contentId);
                }
                // Guardar ruta del archivo antes de eliminar el contenido
                String filePath = content.getImagePath();

                // Eliminar el contenido
                contentRepository.delete(content);
                LOGGER.info("Content with id " + contentId + " was deleted");
                if (filePath != null && !filePath.isEmpty()) {
                    fileStorageService.deleteFile(filePath);
                    LOGGER.info("File related to content with id " + contentId + " was deleted");
                }
                LOGGER.warn("No file related to content with id " + contentId + " was found while deleting content");
                return ResponseEntity.ok("\"Content deleted successfully\"");
            } else {
            	LOGGER.warn("Content with id " + contentId + " was not found when trying to delete it");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
        	LOGGER.error("Error when trying to delete content with id " + contentId + " : " + e.getMessage());
            return ResponseEntity.internalServerError().body("\"Error while attempting to delete content with id: " + contentId + ". " + e.getMessage() + "\"");
        }
    }

    public ResponseEntity<String> updateContent(Long contentId, RequestContentDTO requestContentDTO, MultipartFile image) {
        try {
            Content content = contentRepository.findById(contentId).orElse(null);
            if (content == null) {
            	LOGGER.warn("Content with id " + contentId + " was not found when trying to update it");
                return ResponseEntity.notFound().build();
            }
            content.setTitle(requestContentDTO.getTitle());
            content.setDescription(requestContentDTO.getDescription());
            content.setEstimatedHours(requestContentDTO.getEstimatedHours());
            if (image != null) {
                if(content.getImagePath()!=null && !content.getImagePath().isEmpty())
                {
                    fileStorageService.deleteFile(content.getImagePath());
                    LOGGER.info("File related to content with id " + contentId + " was deleted");
                }
                String filename = fileStorageService.store(image, "content");
                LOGGER.info("File related to content with id " + contentId + " was stored");
                content.setImagePath(filename);
            }
            contentRepository.save(content);
            LOGGER.info("Content with id " + contentId + " was updated");
            return ResponseEntity.ok("\"Contenido actualizado con éxito con ID: " + content.getId() + "\"");
        } catch (Exception e) {
        	LOGGER.error("Error trying to update content with id " + contentId);
            return ResponseEntity.internalServerError().body("\"Error al actualizar el contenido: " + e.getMessage() + "\"");
        }
    }
    
    public ResponseEntity<?> findTeacherContents(String teacherId) {
    	try {
    		List<Content> contents = contentRepository.findByTeacherId(teacherId);
    		if (contents.isEmpty()) {
    			return ResponseEntity.notFound().build();
    		} else {
        		List<ResponseContentDTO> dtos = new ArrayList<>();
    	        for (Content content: contents) {
    	            dtos.add(new ResponseContentDTO(content.getId(),content.getTitle(), content.getDescription(), content.getEstimatedHours(), content.getImagePath(),CommentConverter.ListEntityToListDTO(content.getComments())));
    	        }
    			return ResponseEntity.ok(dtos);
    		}
    	} catch (Exception e) {
    		LOGGER.error("Error trying to find contents of teacher with id " + teacherId);
    		return ResponseEntity.internalServerError().body("\"Error attempting to find contents of theacher with id: " + teacherId + ". " +  e.getMessage() + "\"");
    	}
    }

    public ResponseEntity<Resource> getFile(Long contentId) {
        try {
            Optional<Content> optionalContent = contentRepository.findById(contentId);
            if(optionalContent.isPresent())
            {
                Resource file = fileStorageService.loadAsResource(optionalContent.get().getImagePath());
                String contentType = Files.probeContentType(file.getFile().toPath());
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                        .body(file);
            }
            else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
        	LOGGER.error("Error trying to find file of content with id " + contentId);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<?> searchContent(String filter) {
        List<Content> contents = contentRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(filter, filter);
        if(contents.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("\"There are not such content registered\"");
        } else {
            List<ResponseContentDTO> dtos = new ArrayList<>();
            for(Content content: contents){
                dtos.add(
                	new ResponseContentDTO(
                			content.getId(),
                			content.getTitle(), 
                			content.getDescription(), 
                			content.getEstimatedHours(), 
                			content.getImagePath(),
                			CommentConverter.ListEntityToListDTO(content.getComments())
                	)
                );
            }
            return  ResponseEntity
                    .status(HttpStatus.OK)
                    .body(dtos);
        }
    }

    public ResponseEntity<List<ResponseContentDTO>> getContentsByCourseId(Long courseId) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            List<Content> contents = course.getContents();
            List<ResponseContentDTO> dtos = new ArrayList<>();
            for (Content content: contents) {
                dtos.add(new ResponseContentDTO(content.getId(),content.getTitle(), content.getDescription(), content.getEstimatedHours(), content.getImagePath(),CommentConverter.ListEntityToListDTO(content.getComments())));
            }
            return ResponseEntity.ok(dtos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
