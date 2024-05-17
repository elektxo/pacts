package com.aula.dto.converter.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aula.dto.CourseContentDTO;
import com.aula.dto.CourseDTO;
import com.aula.entity.Content;
import com.aula.entity.Course;
import com.aula.entity.CourseContent;

@Component
public class CourseConverter extends BaseConverterImpl<CourseDTO, Course> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CourseConverter.class);
	
	@Override
	public Course dtoToEntity(CourseDTO courseDTO) {
		Course course = new Course();
		course.setTitle(courseDTO.getTitle());
		course.setDescription(courseDTO.getDescription());
		course.setPrice(courseDTO.getPrice());
		course.setFree(courseDTO.getFree());
		return course;	
	}
	
	@Override
	public Course dtoToEntity(CourseDTO courseDTO, Course course) {
		course.setTitle(courseDTO.getTitle());
		course.setDescription(courseDTO.getDescription());
		course.setPrice(courseDTO.getPrice());
		course.setFree(courseDTO.getFree());
		return course;	
	}
	
	@Override
	public CourseDTO entityToDTO(Course course) {
		CourseDTO courseDTO = new CourseDTO();
		courseDTO.setId(course.getId());
		courseDTO.setTitle(course.getTitle());
		courseDTO.setDescription(course.getDescription());
		courseDTO.setPrice(course.getPrice());
		courseDTO.setFree(course.getFree());
		courseDTO.setImagePath(course.getImagePath());
		courseDTO.setTeacherId(course.getTeacherId());
		List<CourseContentDTO> courseContentsDTO = new ArrayList<>();
		if (!course.getCourseContents().isEmpty()) {
			for (CourseContent courseContent : course.getCourseContents()) {
				CourseContentDTO courseContentDTO = new CourseContentDTO();
				Content content = courseContent.getContent();
				courseContentDTO.setId(content.getId());
				courseContentDTO.setTitle(content.getTitle());
				courseContentDTO.setDescription(content.getDescription());
				courseContentDTO.setEstimatedHours(content.getEstimatedHours());
				courseContentDTO.setImagePath(content.getImagePath());
				courseContentDTO.setOrderInCourse(courseContent.getOrderInCourse());
				courseContentsDTO.add(courseContentDTO);
			}
		} else {
			LOGGER.warn("No contents were found when fetching the course with id: " + course.getId());
		}
		courseDTO.setContents(courseContentsDTO);
		courseDTO.setIdReviewable(course.getReviewable().getId());
		return courseDTO;
	}
}
