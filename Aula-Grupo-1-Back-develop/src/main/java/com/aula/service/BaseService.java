package com.aula.service;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;

import com.aula.dto.BaseDTO;

public interface BaseService<DTO extends BaseDTO, ID extends Serializable> {

	ResponseEntity<?> save(DTO dto);
	ResponseEntity<?> findAll();
	ResponseEntity<?> findById(ID id);	
	ResponseEntity<?> update(ID id, DTO dto);
	ResponseEntity<?> delete(ID id);
	
}
