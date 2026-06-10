package com.cyphervault.fabric.common.exception;

import com.cyphervault.fabric.common.exception.AppException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends AppException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}