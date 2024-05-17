package com.aula.entity;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="content")
public class Content {
    
	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Integer estimatedHours;
    private String teacherId;

    @Column(name = "image_path")
    private String imagePath;

    @OneToMany(mappedBy ="content")
    private List<CourseContent> courseContents;
    
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

	public List<Course> getCourses() {
		List<Course> courses = new ArrayList<>();
		for (CourseContent courseContent : this.getCourseContents()) {
			courses.add(courseContent.getCourse());
		}
		return courses;
	}
}
