package com.cyphervault.fabric.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BalanceProofRequest {

    @NotBlank
    private String clientRequestId;

    @NotBlank
    private String userId;

    @NotBlank
    private String keyId;

    @NotBlank
    private String currency;

    @NotBlank
    private String nonce;

    @NotBlank
    private String userSignatureBase64;
}