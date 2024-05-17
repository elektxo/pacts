package com.aula.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(uniqueConstraints = { 
		@UniqueConstraint(columnNames = { "course_registration_id", "content_id" })
})
public class CourseRegistrationContent extends BaseEntity {
	
	@ManyToOne
	private CourseRegistration courseRegistration;
	
	@ManyToOne
	private Content content;
	
	@NotNull
	private Boolean completed;
}
