package com.aula.exceptions;

import com.aula.dto.ErrorResponseDTO;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

//  Function to build exceptions responses into ErrorResponseDTO
    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, Exception exception, WebRequest webRequest) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(exception.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorResponse, status);
    }

//    Custom Exceptions
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handlerDuplicateResourceException(DuplicateResourceException exception, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.CONFLICT, exception, webRequest);
    }

    @ExceptionHandler(CreationUserException.class)
    public ResponseEntity<ErrorResponseDTO> handlerCreationUserException(CreationUserException exception, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception, webRequest);
    }

//    Normal Exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handlerArgumentException(IllegalArgumentException exception, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception, webRequest);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handlerBadRequestException(BadRequestException exception, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception, webRequest);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlerNotFoundException(NotFoundException exception, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception, webRequest);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors;
        errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error ->{
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handlerGlobalException(Exception exception, WebRequest webRequest) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception, webRequest);
    }
}
