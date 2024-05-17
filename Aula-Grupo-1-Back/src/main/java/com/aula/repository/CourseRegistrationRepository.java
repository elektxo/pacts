package com.aula.repository;

import java.util.List;
import java.util.Optional;

import com.aula.entity.Course;
import com.aula.entity.CourseRegistration;

public interface CourseRegistrationRepository extends BaseRepository<CourseRegistration, Long> {
	
	Optional<CourseRegistration> findByCourseAndStudentId(Course course, String studentId);
	List<CourseRegistration> findByStudentId(String studentId);
}
