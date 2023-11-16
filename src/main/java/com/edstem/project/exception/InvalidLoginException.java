package com.edstem.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException() {
        super("Invalid login");
    }
}
