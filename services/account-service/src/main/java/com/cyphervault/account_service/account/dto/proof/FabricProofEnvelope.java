package com.cyphervault.account_service.account.dto.proof;

import java.util.List;

public class FabricProofEnvelope {

    private String payloadJson;
    private String payloadHash;
    private String fabricTxId;

    private String channelName;
    private String chaincodeName;
    private String contractName;
    private String functionName;

    private String endorsedTransactionEnvelopeBase64;
    private boolean submitted;
    private String commitStatus;

    private List<FabricEndorsementInfo> endorsements;

    public FabricProofEnvelope() {
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public String getPayloadHash() {
        return payloadHash;
    }

    public void setPayloadHash(String payloadHash) {
        this.payloadHash = payloadHash;
    }

    public String getFabricTxId() {
        return fabricTxId;
    }

    public void setFabricTxId(String fabricTxId) {
        this.fabricTxId = fabricTxId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChaincodeName() {
        return chaincodeName;
    }

    public void setChaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getEndorsedTransactionEnvelopeBase64() {
        return endorsedTransactionEnvelopeBase64;
    }

    public void setEndorsedTransactionEnvelopeBase64(String endorsedTransactionEnvelopeBase64) {
        this.endorsedTransactionEnvelopeBase64 = endorsedTransactionEnvelopeBase64;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public String getCommitStatus() {
        return commitStatus;
    }

    public void setCommitStatus(String commitStatus) {
        this.commitStatus = commitStatus;
    }

    public List<FabricEndorsementInfo> getEndorsements() {
        return endorsements;
    }

    public void setEndorsements(List<FabricEndorsementInfo> endorsements) {
        this.endorsements = endorsements;
    }
}