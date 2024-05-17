//package com.aula.dto.converter;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import com.aula.dto.CourseDTO;
//import com.aula.dto.ResponseContentDTO;
//import com.aula.entity.Content;
//import com.aula.entity.Course;
//import com.aula.entity.Reviewable;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.List;
//
//class CourseConverterTest {
//
//	private CourseConverter courseConverter;
//	
//	@BeforeEach
//	void setUp() {
//		courseConverter = new CourseConverter();
//	}
//	
//	@Test
//	void testDtoToEntity() {
//		CourseDTO dto = new CourseDTO();
//		dto.setTitle("Spring");
//		dto.setDescription("Introduction to Spring");
//		dto.setPrice(20.20);
//		dto.setFree(false);
//		
//		Course course = courseConverter.dtoToEntity(dto);
//		
//		assertEquals(course.getTitle(), dto.getTitle());
//		assertEquals(course.getDescription(), dto.getDescription());
//		assertEquals(course.getPrice(), dto.getPrice());
//		assertEquals(course.getFree(), dto.getFree());
//	}
//	
//	@Test
//	void testDtoToEntityUpdating() {
//		CourseDTO dto = new CourseDTO();
//		dto.setTitle("Spring");
//		dto.setDescription("Introduction to Spring");
//		dto.setPrice(20.20);
//		dto.setFree(false);
//		Course course = new Course();
//		course.setTitle("Django");
//		course.setDescription("Framework for Python developers");
//		course.setPrice(30.30);
//		course.setFree(false);
//		
//		Course updatedCourse = courseConverter.dtoToEntity(dto, course);
//		
//		assertEquals(updatedCourse.getTitle(), dto.getTitle());
//		assertEquals(updatedCourse.getDescription(), dto.getDescription());
//		assertEquals(updatedCourse.getPrice(), dto.getPrice());
//		assertEquals(updatedCourse.getFree(), dto.getFree());
//	}
//	
//	@Test
//	void testEntityToDTO() {
//		Course course = new Course();
//		course.setId((long)2);
//		course.setTitle("Django");
//		course.setDescription("Framework for Python developers");
//		course.setPrice(30.30);
//		course.setFree(false);
//		course.setTeacherId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f");
//		Content content = Content.builder()
//              .title("Pip")
//              .description("Introduction to package management")
//              .estimatedHours(2)
//              .imagePath(null)
//              .teacherId("id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f")
//              .id((long)4)
//              .build();
//		course.setContents(List.of(content));
//		Reviewable reviewable = new Reviewable();
//		reviewable.setId((long)2);
//		course.setReviewable(reviewable);
//		CourseDTO dto = courseConverter.entityToDTO(course);
//		assertEquals(dto.getTitle(), course.getTitle());
//		assertEquals(dto.getDescription(), course.getDescription());
//		assertEquals(dto.getPrice(), course.getPrice());
//		assertEquals(dto.getFree(), course.getFree());
//		assertEquals(dto.getId(), course.getId());
//		assertEquals(dto.getTeacherId(), course.getTeacherId());
//		assertEquals(dto.getIdReviewable(), course.getReviewable().getId());
//		for (ResponseContentDTO contentDTO : dto.getContents()) {
//			assertEquals(contentDTO.getId(), content.getId());
//			assertEquals(contentDTO.getTitle(), content.getTitle());
//			assertEquals(contentDTO.getDescription(), content.getDescription());
//			assertEquals(contentDTO.getEstimatedHours(), content.getEstimatedHours());
//			assertEquals(contentDTO.getImagePath(), content.getImagePath());
//			assertEquals(contentDTO.getId(), content.getId());
//		}
//	}
//	
//}
