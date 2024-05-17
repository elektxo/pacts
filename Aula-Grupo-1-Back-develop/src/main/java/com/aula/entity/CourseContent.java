package com.aula.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(uniqueConstraints = { 
	@UniqueConstraint(columnNames = { "course_id", "content_id" }), 
	@UniqueConstraint(columnNames = { "course_id", "order_in_course" }) 
})
public class CourseContent extends BaseEntity{
	
	@ManyToOne
	private Course course;
	
	@ManyToOne
	private Content content;
	
	@NotNull
	@Min(0)
	private Integer orderInCourse;
}
