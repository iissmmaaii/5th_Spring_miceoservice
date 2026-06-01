package com.cyphervault.fabric.common.exception;

import com.cyphervault.account_service.common.exception.AppException;
import org.springframework.http.HttpStatus;

public class ConflictException extends AppException {

    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}