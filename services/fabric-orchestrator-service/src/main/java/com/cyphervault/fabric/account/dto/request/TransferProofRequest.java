package com.cyphervault.fabric.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferProofRequest {

    @NotBlank
    private String clientRequestId;

    @NotBlank
    private String senderUserId;

    @NotBlank
    private String senderKeyId;

    @NotBlank
    private String receiverUserId;

    @NotBlank
    private String amountMinor;

    @NotBlank
    private String currency;

    @NotBlank
    private String nonce;

    @NotBlank
    private String userSignatureBase64;
}