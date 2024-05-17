package com.aula.repository;

import com.aula.entity.Content;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
	
	List<Content> findByTeacherId(String teacherId);

	List<Content> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}
