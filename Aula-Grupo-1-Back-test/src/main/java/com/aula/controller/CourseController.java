package com.aula.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aula.dto.CourseDTO;
import com.aula.dto.converter.impl.CourseConverter;
import com.aula.entity.Course;
import com.aula.service.impl.CourseServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("api/v1/courses")
public class CourseController
	extends BaseController<CourseDTO, Course, CourseConverter, CourseServiceImpl> {

	@Autowired
	private CourseServiceImpl courseService;
	
	@GetMapping("/search")
	public ResponseEntity<?> search(@RequestParam String filter) {
		return service.search(filter);
	}
	
	@GetMapping("/find-free")
	public ResponseEntity<?> findByFree(@RequestParam Boolean free) {
		return service.findByFree(free);
	}

	@PostMapping(value = {""}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} )
	public ResponseEntity<?> createCourse(
			@Valid @RequestPart("course") CourseDTO courseDTO,
			@NotNull @RequestPart("image") final MultipartFile image) {
		return service.save(courseDTO, image);
	}

	@GetMapping("teacher/{teacherId}/courses")
	public ResponseEntity<?> getTeacherCourses(@PathVariable String teacherId) {
		return courseService.findTeacherCourses(teacherId);
	}
	
	@Operation(
			summary = "Updates the course, adding the content indicated", 
			description = "Takes the the id provided in the url to fetch the course and adds the content corresponding with the id provided in the query parameter. Returns the course representation after presistance.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "201", description = "Successfully added the course"), 
	    @ApiResponse(responseCode = "400", description = "Bad request - Something about the request was wrong. Please, make sure that everything conforms with the documentation for this operation."),
	    @ApiResponse(responseCode = "404", description = "Not found - There where no registers for that course or content")
	})
	@PutMapping("/{courseId}/add-content")
	public ResponseEntity<?> addContent(
		@PathVariable Long courseId,  
		@RequestParam @Positive @NotNull Long contentId) {
			return service.addContent(courseId, contentId);
	}
	
	
	@Operation(
			summary = "Updates the course, removing the content indicated.", 
			description = "Takes the the id provided in the url to fetch the course and removes the content corresponding with the id provided in the query parameter. Returns the course representation after presistance")
	@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully removed the course"), 
        @ApiResponse(responseCode = "400", description = "Bad request - Something about the request was wrong. Please, make sure that everything conforms with the documentation for this operation"),
        @ApiResponse(responseCode = "404", description = "Not found - There where no registers for that course or content")
    })
	@PutMapping("/{courseId}/remove-content")
	public ResponseEntity<?> 
		removeContent(@PathVariable Long courseId,  
				 @RequestParam @Positive @NotNull Long contentId) {
					return service.removeContent(courseId,  contentId);
	}
	
	@Operation(
			summary = "Updates the course, adding the student indicated", 
			description = "Takes the the id provided in the url to fetch the course and adds the student corresponding with the id provided in the query parameter. Returns the course representation after presistance.")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "201", description = "Successfully added the course"), 
	    @ApiResponse(responseCode = "400", description = "Bad request - Something about the request was wrong. Please, make sure that everything conforms with the documentation for this operation."),
	    @ApiResponse(responseCode = "404", description = "Not found - There where no registers for that course or student")
	})
	@PutMapping("/{courseId}/add-student")
	public ResponseEntity<?> 
		addStudent(@PathVariable Long courseId,  
				 @RequestParam @NotNull String studentId) {
					return service.addStudent(courseId, studentId);
	}
	
	
	@Operation(
			summary = "Updates the course, removing the student indicated.", 
			description = "Takes the the id provided in the url to fetch the course and removes the student corresponding with the id provided in the query parameter. Returns the course representation after presistance")
	@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully removed the course"), 
        @ApiResponse(responseCode = "400", description = "Bad request - Something about the request was wrong. Please, make sure that everything conforms with the documentation for this operation"),
        @ApiResponse(responseCode = "404", description = "Not found - There where no registers for that course or student")
    })
	@PutMapping("/{courseId}/remove-student")
	public ResponseEntity<?> 
		removeStudent(@PathVariable Long courseId,  
				 @RequestParam @NotNull String studentId) {
					return service.removeStudent(courseId, studentId);
	}
	
	@GetMapping("/{courseId}/students")
	public ResponseEntity<?>
		registeredStudents(@PathVariable Long courseId) {
			return service.getStudents(courseId);
	}

	@GetMapping("/student/{studentId}")
	public ResponseEntity<?> getCoursesByStudentId(@PathVariable String studentId) {
		return service.getAllByStudentId(studentId);
	}

	@GetMapping("/{courseId}/student/{studentId}")
	public ResponseEntity<?> getStudentCourses(@PathVariable String studentId, @PathVariable Long courseId) {
		return service.getCourseByStudentId(courseId,studentId);
	}

	@GetMapping("/image/{courseId}")
	public ResponseEntity<Resource> getFile(@PathVariable Long courseId) {
		return service.getFile(courseId);
	}
}
