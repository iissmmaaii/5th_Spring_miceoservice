package com.cyphervault.account_service.common.exception;

import com.cyphervault.account_service.common.logging.MdcKeys;
import com.cyphervault.account_service.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "APP_EXCEPTION status={} message={} path={} correlationId={}",
                ex.getStatus().value(),
                ex.getMessage(),
                request.getRequestURI(),
                MDC.get(MdcKeys.CORRELATION_ID)
        );

        return build(
                ex.getStatus(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "VALIDATION_FAILED path={} errors={} correlationId={}",
                request.getRequestURI(),
                ex.getBindingResult().getFieldErrorCount(),
                MDC.get(MdcKeys.CORRELATION_ID)
        );

        return build(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Validation failed"
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "TYPE_MISMATCH parameter={} value={} path={} correlationId={}",
                ex.getName(),
                ex.getValue(),
                request.getRequestURI(),
                MDC.get(MdcKeys.CORRELATION_ID)
        );

        return build(
                HttpStatus.BAD_REQUEST,
                "Invalid request parameter"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error(
                "INTERNAL_SERVER_ERROR path={} correlationId={}",
                request.getRequestURI(),
                MDC.get(MdcKeys.CORRELATION_ID),
                ex
        );

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error"
        );
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String message
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message(message)
                .status(status.value())
                .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }
}