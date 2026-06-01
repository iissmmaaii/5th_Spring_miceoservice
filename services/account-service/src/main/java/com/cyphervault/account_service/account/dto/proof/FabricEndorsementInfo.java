package com.cyphervault.account_service.account.dto.proof;

public class FabricEndorsementInfo {

    private String mspId;
    private String endorserCertificateBase64;
    private String proposalResponsePayloadBase64;
    private String signatureBase64;

    public FabricEndorsementInfo() {
    }

    public String getMspId() {
        return mspId;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }

    public String getEndorserCertificateBase64() {
        return endorserCertificateBase64;
    }

    public void setEndorserCertificateBase64(String endorserCertificateBase64) {
        this.endorserCertificateBase64 = endorserCertificateBase64;
    }

    public String getProposalResponsePayloadBase64() {
        return proposalResponsePayloadBase64;
    }

    public void setProposalResponsePayloadBase64(String proposalResponsePayloadBase64) {
        this.proposalResponsePayloadBase64 = proposalResponsePayloadBase64;
    }

    public String getSignatureBase64() {
        return signatureBase64;
    }

    public void setSignatureBase64(String signatureBase64) {
        this.signatureBase64 = signatureBase64;
    }
}