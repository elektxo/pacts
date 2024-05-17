package com.aula.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public record UserResponseDTO (
        String id,
        String username,
        String firstName,
        String lastName,
        String email,
        Boolean emailVerified
) {}