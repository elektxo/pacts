package com.aula.repository;

import com.aula.entity.Content;
import com.aula.entity.CourseRegistration;
import com.aula.entity.CourseRegistrationContent;

public interface CourseRegistrationContentRepository 
	extends BaseRepository<CourseRegistrationContent, Long> {
	
	CourseRegistrationContent findByCourseRegistrationAndContent(CourseRegistration courseReg, Content content);
}
