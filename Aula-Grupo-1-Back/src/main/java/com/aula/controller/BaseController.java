package com.aula.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.aula.dto.BaseDTO;
import com.aula.dto.converter.impl.BaseConverterImpl;
import com.aula.entity.BaseEntity;
import com.aula.service.impl.BaseServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

public abstract class BaseController
	<
		DTO extends BaseDTO, 
		E extends BaseEntity, 
		C extends BaseConverterImpl<DTO, E>,  
		S extends BaseServiceImpl<DTO, Long, E, C>
	> {
	
	@Autowired
	protected S service;
	
	@Operation(
		summary = "Get all the registers available for the given entity", 
		description = "The resource has been fetched and transmitted in the message body as list of representations of the entity")
	@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"), 
        @ApiResponse(responseCode = "404", description = "Not found - There where no registers for that entity")
    })
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		return service.findAll();
	}
	
	
	@Operation(
		summary = "Creates a new register of the entity", 
		description = "Takes the body of the request to create and register an instance of the entity. Returns the resource representation after presistance.")
	@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created"), 
        @ApiResponse(responseCode = "400", description = "Bad request - Something about the request was wrong. Please, make sure that everything conforms with the documentation for this operation.")
    })
	@PostMapping("")
	public ResponseEntity<?> save(@Valid @RequestBody DTO dto) {
		return service.save(dto);
	}
	
	
	@Operation(
		summary = "Get the register for the entity that corresponds with the given id", 
		description = "Takes the id provided in the url as criteria for fetching one register of the entity, retrieving its representation.")
	@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"), 
        @ApiResponse(responseCode = "404", description = "Not found - There where no registers for that entity")
    })
	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable Long id) {
		return service.findById(id);
	}
	
	
	@Operation(
		summary = "Updates the register of the entity corresponding with the given id", 
		description = "Takes the the id provided in the url and the body of the request to fetch and update an instance of the entity. Returns the resource representation after presistance.")
	@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully updated"), 
        @ApiResponse(responseCode = "400", description = "Bad request - Something about the request was wrong. Please, make sure that everything conforms with the documentation for this operation."),
        @ApiResponse(responseCode = "404", description = "Not found - There where no registers for that entity")
    })
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody DTO dto) {
		return service.update(id, dto);
	}
	
	
	@Operation(
		summary = "Deletes the register of the entity corresponding with the given id", 
		description = "Takes the the id provided in the url and the body of the request to delete an instance of the entity.")
	@ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Operation performed successfully"), 
    })
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		return service.delete(id);
	}
	
}
