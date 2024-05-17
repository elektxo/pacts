package com.aula.repository;

import java.util.List;

import com.aula.entity.Content;
import com.aula.entity.Course;

public interface CourseRepository extends BaseRepository<Course, Long> {

	List<Course> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
	
	List<Course> findByFree(Boolean free);

	List<Course> findByTeacherId(String teacherId);
}
