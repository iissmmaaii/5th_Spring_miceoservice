package com.cyphervault.fabric.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OpenAccountProofRequest {

    @NotBlank
    private String clientRequestId;

    @NotBlank
    private String ownerUserId;

    @NotBlank
    private String ownerKeyId;

    @NotBlank
    private String openingBalanceMinor;

    @NotBlank
    private String currency;

    @NotBlank
    private String nonce;

    @NotBlank
    private String userSignatureBase64;
}