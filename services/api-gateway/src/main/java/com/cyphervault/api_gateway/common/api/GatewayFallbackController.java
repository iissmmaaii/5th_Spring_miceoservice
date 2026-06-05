package com.cyphervault.api_gateway.common.api;

import com.cyphervault.api_gateway.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

    @RequestMapping("/iam")
    public ResponseEntity<ErrorResponse> iamFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.builder()
                        .success(false)
                        .message("IAM service is temporarily unavailable")
                        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .build());
    }

    @RequestMapping("/account")
    public ResponseEntity<ErrorResponse> accountFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.builder()
                        .success(false)
                        .message("Account service is temporarily unavailable")
                        .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .build());
    }
}