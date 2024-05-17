package com.aula.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="course")
public class Course extends BaseEntity {
	
	@NotBlank
	@NotNull
	private String title;

	@NotBlank
	@NotNull
	private String description;
	
	@Positive
	@NotNull
	private Double price; 
	
	@NotNull
	private Boolean free;

	private String teacherId;
	
	@OneToOne
	private Reviewable reviewable;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="course", orphanRemoval=true)
	private List<CourseContent> courseContents; 
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="course", orphanRemoval=true)
	private List<CourseRegistration> registrations;

	@Column(name = "image_path")
	private String imagePath;

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments;
	
	public List<Content> getContents() {
		List<Content> contents = new ArrayList<>();
		for (CourseContent courseContent : this.getCourseContents()) {
			contents.add(courseContent.getContent());
		}
		return contents;
	}
	
}
