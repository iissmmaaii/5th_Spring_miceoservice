package com.cyphervault.account_service.account.dto.request;

import jakarta.validation.constraints.NotBlank;

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

    public String getClientRequestId() { return clientRequestId; }
    public void setClientRequestId(String clientRequestId) { this.clientRequestId = clientRequestId; }

    public String getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(String ownerUserId) { this.ownerUserId = ownerUserId; }

    public String getOwnerKeyId() { return ownerKeyId; }
    public void setOwnerKeyId(String ownerKeyId) { this.ownerKeyId = ownerKeyId; }

    public String getOpeningBalanceMinor() { return openingBalanceMinor; }
    public void setOpeningBalanceMinor(String openingBalanceMinor) { this.openingBalanceMinor = openingBalanceMinor; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getNonce() { return nonce; }
    public void setNonce(String nonce) { this.nonce = nonce; }

    public String getUserSignatureBase64() { return userSignatureBase64; }
    public void setUserSignatureBase64(String userSignatureBase64) { this.userSignatureBase64 = userSignatureBase64; }
}