package com.cyphervault.account_service.account.dto.request;

import jakarta.validation.constraints.NotBlank;

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

    public String getClientRequestId() { return clientRequestId; }
    public void setClientRequestId(String clientRequestId) { this.clientRequestId = clientRequestId; }

    public String getSenderUserId() { return senderUserId; }
    public void setSenderUserId(String senderUserId) { this.senderUserId = senderUserId; }

    public String getSenderKeyId() { return senderKeyId; }
    public void setSenderKeyId(String senderKeyId) { this.senderKeyId = senderKeyId; }

    public String getReceiverUserId() { return receiverUserId; }
    public void setReceiverUserId(String receiverUserId) { this.receiverUserId = receiverUserId; }

    public String getAmountMinor() { return amountMinor; }
    public void setAmountMinor(String amountMinor) { this.amountMinor = amountMinor; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getNonce() { return nonce; }
    public void setNonce(String nonce) { this.nonce = nonce; }

    public String getUserSignatureBase64() { return userSignatureBase64; }
    public void setUserSignatureBase64(String userSignatureBase64) { this.userSignatureBase64 = userSignatureBase64; }
}