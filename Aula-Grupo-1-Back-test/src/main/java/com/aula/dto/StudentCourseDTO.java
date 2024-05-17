package com.aula.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourseDTO extends BaseDTO {
	
	private String title;
	private String description;
	private Double price;
	private Boolean free;
	private String	teacherId;
	private List<StudentCourseContentDTO> contents;
	private String imagePath;
	private Long idReviewable;
}
