package com.cyphervault.account_service.account.dto.proof;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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


}