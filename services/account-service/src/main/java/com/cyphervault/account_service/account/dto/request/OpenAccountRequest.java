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
public class OpenAccountRequest {

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