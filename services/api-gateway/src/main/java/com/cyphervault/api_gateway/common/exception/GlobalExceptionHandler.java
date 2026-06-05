package com.cyphervault.api_gateway.common.exception;

import com.cyphervault.api_gateway.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException exception
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .status(exception.getStatus().value())
                .build();

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception
    ) {
        log.error("UNEXPECTED_GATEWAY_ERROR", exception);

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message("Internal server error")
                .status(500)
                .build();

        return ResponseEntity
                .internalServerError()
                .body(response);
    }
}