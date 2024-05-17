package com.aula.dto.converter.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.aula.dto.StudentCourseContentDTO;
import com.aula.dto.StudentCourseDTO;
import com.aula.entity.Content;
import com.aula.entity.Course;
import com.aula.entity.CourseContent;
import com.aula.entity.CourseRegistration;
import com.aula.entity.CourseRegistrationContent;

@Component
public class StudentCourseConverter {

	public StudentCourseDTO entityToDTO(CourseRegistration registration) {
		Course course = registration.getCourse();
		StudentCourseDTO studentCourseDTO = new StudentCourseDTO();
		studentCourseDTO.setId(course.getId());
		studentCourseDTO.setTitle(course.getTitle());
		studentCourseDTO.setDescription(course.getDescription());
		studentCourseDTO.setPrice(course.getPrice());
		studentCourseDTO.setFree(course.getFree());
		studentCourseDTO.setImagePath(course.getImagePath());
		studentCourseDTO.setTeacherId(course.getTeacherId());
		List<CourseContent> courseContents = course.getCourseContents();
		List<StudentCourseContentDTO> studentCourseContentsDTO = new ArrayList<>();
		List<CourseRegistrationContent> courseRegCons = registration.getCourseRegistrationContents();
		if (!courseRegCons.isEmpty()) {
			for (CourseRegistrationContent courseRegContent : courseRegCons) {
				StudentCourseContentDTO studentCourseContentDTO = new StudentCourseContentDTO();
				Content content = courseRegContent.getContent();
				studentCourseContentDTO.setId(content.getId());
				studentCourseContentDTO.setTitle(content.getTitle());
				studentCourseContentDTO.setDescription(content.getDescription());
				studentCourseContentDTO.setEstimatedHours(content.getEstimatedHours());
				studentCourseContentDTO.setImagePath(content.getImagePath());
				for (CourseContent courseContent : courseContents) {
					if (courseContent.getContent() == courseRegContent.getContent()) {
						studentCourseContentDTO.setOrderInCourse(courseContent.getOrderInCourse());
					}
				}
				studentCourseContentDTO.setCompleted(courseRegContent.getCompleted());
				studentCourseContentsDTO.add(studentCourseContentDTO);
			}
		}
		studentCourseDTO.setContents(studentCourseContentsDTO);
		studentCourseDTO.setIdReviewable(course.getReviewable().getId());
		return studentCourseDTO;
	}
}
