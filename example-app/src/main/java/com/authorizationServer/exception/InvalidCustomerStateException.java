package com.authorizationServer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCustomerStateException extends RuntimeException {

    public InvalidCustomerStateException(String message) {
        super(message);
    }
}
