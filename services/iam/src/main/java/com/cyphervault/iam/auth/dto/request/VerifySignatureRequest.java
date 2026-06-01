package com.cyphervault.iam.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class VerifySignatureRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Key ID is required")
    private UUID keyId;

    @NotNull(message = "Challenge ID is required")
    private UUID challengeId;

    @NotBlank(message = "Nonce is required")
    private String nonce;



    @NotBlank(message = "Signature is required")
    private String signature;
}