package com.aula.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class CourseDTO extends BaseDTO {

	@NotBlank
	@NotNull
	private String title;
	
	@NotBlank
	@NotNull
	private String description;
	
	@Positive
	@NotNull
	private Double price; 
	
	@NotNull
	private Boolean free;

	private String	teacherId;
	
	private String teacherEmail;
	
	private Long idReviewable;
	
	private List<CourseContentDTO> contents;

	private MultipartFile inputImage;

	private String imagePath;

	private List<CommentResponse> comments;
	
}
