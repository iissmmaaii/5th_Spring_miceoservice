package com.cyphervault.fabric.account.dto.proof;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FabricEndorsementInfo {

    private String mspId;

    private String endorserCertificateBase64;

    private String proposalResponsePayloadBase64;

    private String signatureBase64;
}