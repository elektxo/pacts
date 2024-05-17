package com.aula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourseContentDTO extends BaseDTO {
	
    private Long id;
    private String title;
    private String description;
    private Integer estimatedHours;
    private String imagePath;
	Integer orderInCourse;
	Boolean completed;
}
