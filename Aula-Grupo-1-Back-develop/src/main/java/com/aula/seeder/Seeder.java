package com.aula.seeder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.aula.entity.Content;
import com.aula.entity.Course;
import com.aula.entity.CourseContent;
import com.aula.entity.Reviewable;
import com.aula.repository.ContentRepository;
import com.aula.repository.CourseContentRepository;
import com.aula.repository.CourseRepository;
import com.aula.repository.ReviewableRepository;
import com.aula.service.impl.CourseServiceImpl;

@Component
public class Seeder implements CommandLineRunner {

	@Autowired
    private ContentRepository contentRepository;
    
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private CourseServiceImpl courseService;
	
	@Autowired
	private CourseContentRepository courseContentRepository;
	
	@Autowired
	private ReviewableRepository reviewableRepository;
    
    @Override
    public void run(String... args){
    	// Keycloack users -> 
    	//				first_name: "javier", id: "id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f"
    	//				first_name: "marta", id: "id10e2ac2c-45b0-43cb-88b8-f0ac50dbe48f"
        //Content seeding
    	if (contentRepository.findAll().isEmpty()) {
	    	Content content = new Content();
	        content.setTitle("Aprende Java desde 0");
	        content.setDescription("Curso de iniciación al lenguaje de programación Java");
	        content.setEstimatedHours(20);
			content.setTeacherId("331dd0df-76c0-46cf-b582-597a785a06a7");
			content.setComments(new ArrayList<>());
	        Content content1 = contentRepository.save(content);
	        content = new Content();
	        content.setTitle("Aprende Angular desde 0");
	        content.setDescription("Curso introductorio de Angular");
	        content.setEstimatedHours(16);
			content.setTeacherId("331dd0df-76c0-46cf-b582-597a785a06a7");
			content.setComments(new ArrayList<>());
	        Content content2 = contentRepository.save(content);
			if(courseRepository.findAll().isEmpty()) {
				//Course seeding
				Course course = new Course();
				course.setTitle("Full Stack Developing with Angular and Java");
				course.setDescription("This course serves as an introduction to full stack web developing.");
				course.setPrice(200.40);
				course.setFree(true);
				course.setTeacherId("331dd0df-76c0-46cf-b582-597a785a06a7");
				course.setReviewable(reviewableRepository.save(new Reviewable()));
				course.setComments(new ArrayList<>());
				course = courseRepository.save(course);
				CourseContent courseAddition0 = new CourseContent();
				courseAddition0.setContent(content1);
				courseAddition0.setCourse(course);
				courseAddition0.setOrderInCourse(0);
				courseContentRepository.save(courseAddition0);
				CourseContent courseAddition1 = new CourseContent();
				courseAddition1.setContent(content2);
				courseAddition1.setCourse(course);
				courseAddition1.setOrderInCourse(1);
				courseContentRepository.save(courseAddition1);
				courseService.addStudent(course.getId(), "c73644f1-b3d1-4443-b1ff-5b289f7e8fef");
				
				
				Course course2 = new Course();
				course2.setTitle("Full Stack Developing with NodeJS and React");
				course2.setDescription("This course serves as an introduction to learn react and NodeJS.");
				course2.setPrice(250.00);
				course2.setFree(false);
				course2.setTeacherId("331dd0df-76c0-46cf-b582-597a785a06a7");
				course2.setReviewable(reviewableRepository.save(new Reviewable()));
				course2.setComments(new ArrayList<>());
				course2 = courseRepository.save(course2);
				CourseContent courseAddition2 = new CourseContent();
				courseAddition2.setContent(content1);
				courseAddition2.setCourse(course2);
				courseAddition2.setOrderInCourse(0);
				courseContentRepository.save(courseAddition2);
				CourseContent courseAddition3 = new CourseContent();
				courseAddition3.setContent(content2);
				courseAddition3.setCourse(course2);
				courseAddition3.setOrderInCourse(1);
				courseContentRepository.save(courseAddition3);
				courseService.addStudent(course2.getId(), "c73644f1-b3d1-4443-b1ff-5b289f7e8fef");
				
				Course course3 = new Course();
				course3.setTitle("Java programming for beginners");
				course3.setDescription("This course serves as an introduction to Java.");
				course3.setPrice(100.00);
				course3.setFree(false);
				course3.setTeacherId("331dd0df-76c0-46cf-b582-597a785a06a7");
				course3.setReviewable(reviewableRepository.save(new Reviewable()));
				course3.setComments(new ArrayList<>());
				course3 = courseRepository.save(course3);
				CourseContent courseAddition4 = new CourseContent();
				courseAddition4.setContent(content2);
				courseAddition4.setCourse(course3);
				courseAddition4.setOrderInCourse(0);
				courseContentRepository.save(courseAddition4);
				courseService.addStudent(course3.getId(), "c73644f1-b3d1-4443-b1ff-5b289f7e8fef");
			}

    	}
    }
}