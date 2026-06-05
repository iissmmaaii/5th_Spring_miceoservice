package com.cyphervault.account_service.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferMoneyRequest {

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