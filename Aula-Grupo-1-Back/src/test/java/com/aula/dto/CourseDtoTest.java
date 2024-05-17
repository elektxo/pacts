package com.aula.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.meanbean.test.Configuration;
import org.meanbean.test.ConfigurationBuilder;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseDtoTest {

	@Test
	void testCourseDTO() {
		Configuration configuration = new ConfigurationBuilder().ignoreProperty("inputImage").build();
		
		BeanTester beanTester = new BeanTester();
		beanTester.testBean(CourseDTO.class, configuration);
	}
}
