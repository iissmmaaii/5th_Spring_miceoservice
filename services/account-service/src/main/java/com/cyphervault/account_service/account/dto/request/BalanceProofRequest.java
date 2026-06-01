package com.cyphervault.account_service.account.dto.request;

import jakarta.validation.constraints.NotBlank;

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

    public String getClientRequestId() { return clientRequestId; }
    public void setClientRequestId(String clientRequestId) { this.clientRequestId = clientRequestId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getNonce() { return nonce; }
    public void setNonce(String nonce) { this.nonce = nonce; }

    public String getUserSignatureBase64() { return userSignatureBase64; }
    public void setUserSignatureBase64(String userSignatureBase64) { this.userSignatureBase64 = userSignatureBase64; }
}