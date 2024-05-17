package com.aula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentCompletedDTO extends BaseDTO {
	
	Long contentId;
	Long courseId;
	String studentId;
	Boolean completed;
}
