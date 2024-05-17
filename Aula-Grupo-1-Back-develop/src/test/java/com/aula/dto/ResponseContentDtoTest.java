package com.aula.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.meanbean.test.BeanTester;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResponseContentDtoTest {

	@Test
	void testCourseDTO() {
		BeanTester beanTester = new BeanTester();
		beanTester.testBean(ResponseContentDTO.class);
	}
}
