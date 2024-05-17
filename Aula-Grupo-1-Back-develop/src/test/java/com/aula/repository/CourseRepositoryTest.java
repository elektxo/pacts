package com.aula.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

import com.aula.entity.Course;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestMethodOrder(OrderAnnotation.class)
class CourseRepositoryTest {

	@Autowired
	private CourseRepository courseRepository;
	
	@BeforeEach
	public void setUp() {
		Course course = new Course();
		course.setTitle("Java");
		course.setDescription("Introduction to Java");
		course.setPrice(20.00);
		course.setFree(false);
		course.setImagePath(null);
		course.setTeacherId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f");
		courseRepository.save(course);
	}
	
	@Test
	@Order(1)
	void testFindById() {
		Optional<Course> optCourse = courseRepository.findById((long)1);
		assertTrue(optCourse.isPresent());
	}
	
	@Test
	@Order(2)
	void testFindAll() {
		List<Course> optCourse = courseRepository.findAll();
		assertFalse(optCourse.isEmpty());
	}
	
	@Test
	@Order(3)
	void testDeleteById() {
		Optional<Course> optCourse = courseRepository.findById((long)3);
		assertTrue(optCourse.isPresent());
		courseRepository.deleteById((long)3);
		optCourse = courseRepository.findById((long)3);
		assertFalse(optCourse.isPresent());
	}
	
	@Test
	@Order(4)
	void testUpdateById() {
		Optional<Course> optCourse = courseRepository.findById((long)4);
		assertTrue(optCourse.isPresent());
		Course course = optCourse.get();
		var oldTitle = course.getTitle();
		var newTitle = oldTitle + "difference";
		course.setTitle(newTitle);
		Course newCourse = courseRepository.save(course);
		assertEquals(newCourse.getTitle(), newTitle);
	}
	
	@Test
	@Order(5)
	void tstFindByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase() {
		List<Course> courses = courseRepository
				.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("ja", "ZX");
		assertFalse(courses.isEmpty());
		courses = courseRepository
				.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("KL", "ZX");
		assertTrue(courses.isEmpty());
		courses = courseRepository
				.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("KL", "TO");
		assertFalse(courses.isEmpty());
	}
	
	@Test
	@Order(6)
	void testFindByFree() {
		List<Course> courses = courseRepository.findByFree(true);
		assertTrue(courses.isEmpty());
		courses = courseRepository.findByFree(false);
		assertFalse(courses.isEmpty());
	}
	
	
	
}
