package com.cyphervault.iam.auth.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must not exceed 120 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 160, message = "Email must not exceed 160 characters")
    private String email;

    @NotBlank(message = "Device ID is required")
    @Size(max = 140, message = "Device ID must not exceed 140 characters")
    private String deviceId;

    @NotBlank(message = "Public key is required")
    private String publicKeyPem;
}