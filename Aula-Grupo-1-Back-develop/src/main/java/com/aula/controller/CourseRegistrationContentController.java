package com.aula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aula.dto.ContentCompletedDTO;
import com.aula.service.impl.CourseRegistrationContentService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/v1/course-registration-contents")
@AllArgsConstructor
@CrossOrigin(origins= {"http://localhost:4200"})
public class CourseRegistrationContentController {

	@Autowired
	private CourseRegistrationContentService courseRegConService;
	
	@PutMapping("/complete")
    public ResponseEntity<?> updateContent(@RequestBody ContentCompletedDTO contentCompletedDTO) {
        return courseRegConService.complete(contentCompletedDTO);
    }
}
