package com.aula.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class CreationUserException extends RuntimeException {
    public CreationUserException(String message) {
        super(message);
    }
}