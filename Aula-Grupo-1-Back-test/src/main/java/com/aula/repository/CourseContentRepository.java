package com.aula.repository;

import com.aula.entity.Content;
import com.aula.entity.Course;
import com.aula.entity.CourseContent;

public interface CourseContentRepository extends BaseRepository<CourseContent, Long>{
	
	CourseContent findByContentAndCourse(Content content, Course course);
}
