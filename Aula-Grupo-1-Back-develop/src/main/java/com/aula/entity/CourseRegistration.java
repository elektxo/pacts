package com.aula.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "course", "studentId" }) })
public class CourseRegistration extends BaseEntity {

	@ManyToOne
	@JoinColumn(name="course_id")
	private Course course;
	
	private String studentId;
	
	@OneToMany(mappedBy = "courseRegistration", orphanRemoval=true)
	private List<CourseRegistrationContent> courseRegistrationContents;
}
