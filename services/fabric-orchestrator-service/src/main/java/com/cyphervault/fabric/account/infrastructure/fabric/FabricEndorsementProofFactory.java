package com.cyphervault.fabric.account.infrastructure.fabric;

import com.cyphervault.fabric.account.dto.proof.FabricEndorsementInfo;
import com.cyphervault.fabric.account.dto.proof.FabricProofEnvelope;
import com.google.protobuf.ByteString;
import org.hyperledger.fabric.client.Transaction;
import org.hyperledger.fabric.protos.common.Common.Envelope;
import org.hyperledger.fabric.protos.common.Common.Payload;
import org.hyperledger.fabric.protos.msp.Identities.SerializedIdentity;
import org.hyperledger.fabric.protos.peer.ProposalResponsePackage.Endorsement;
import org.hyperledger.fabric.protos.peer.ProposalResponsePackage.ProposalResponsePayload;
import org.hyperledger.fabric.protos.peer.ProposalPackage.ChaincodeAction;
import org.hyperledger.fabric.protos.peer.TransactionPackage.ChaincodeActionPayload;
import org.hyperledger.fabric.protos.peer.TransactionPackage.ChaincodeEndorsedAction;
import org.hyperledger.fabric.protos.peer.TransactionPackage.TransactionAction;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class FabricEndorsementProofFactory {

    public FabricProofEnvelope fromEndorsedTransaction(
            Transaction endorsedTransaction,
            String channelName,
            String chaincodeName,
            String contractName,
            String functionName,
            boolean submitted,
            String commitStatus
    ) {
        try {
            String payloadJson = new String(
                    endorsedTransaction.getResult(),
                    StandardCharsets.UTF_8
            );

            String payloadHash = sha256Hex(payloadJson);
            String fabricTxId = endorsedTransaction.getTransactionId();

            byte[] envelopeBytes = endorsedTransaction.getBytes();

            List<FabricEndorsementInfo> endorsements =
                    extractEndorsements(envelopeBytes);

            String envelopeBase64 =
                    Base64.getEncoder().encodeToString(envelopeBytes);

            return FabricProofEnvelope.builder()
                    .payloadJson(payloadJson)
                    .payloadHash(payloadHash)
                    .fabricTxId(fabricTxId)
                    .channelName(channelName)
                    .chaincodeName(chaincodeName)
                    .contractName(contractName)
                    .functionName(functionName)
                    .endorsedTransactionEnvelopeBase64(envelopeBase64)
                    .submitted(submitted)
                    .commitStatus(commitStatus)
                    .endorsements(endorsements)
                    .build();

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Failed to create Fabric endorsement proof",
                    ex
            );
        }
    }

    private List<FabricEndorsementInfo> extractEndorsements(
            byte[] envelopeBytes
    ) throws Exception {

        Envelope envelope = Envelope.parseFrom(envelopeBytes);
        Payload payload = Payload.parseFrom(envelope.getPayload());

        org.hyperledger.fabric.protos.peer.TransactionPackage.Transaction tx =
                org.hyperledger.fabric.protos.peer.TransactionPackage.Transaction
                        .parseFrom(payload.getData());

        if (tx.getActionsCount() == 0) {
            throw new IllegalStateException("Transaction has no actions");
        }

        TransactionAction transactionAction = tx.getActions(0);

        ChaincodeActionPayload chaincodeActionPayload =
                ChaincodeActionPayload.parseFrom(transactionAction.getPayload());

        ChaincodeEndorsedAction endorsedAction =
                chaincodeActionPayload.getAction();

        ByteString proposalResponsePayloadBytes =
                endorsedAction.getProposalResponsePayload();

        ProposalResponsePayload proposalResponsePayload =
                ProposalResponsePayload.parseFrom(proposalResponsePayloadBytes);

        ChaincodeAction chaincodeAction =
                ChaincodeAction.parseFrom(proposalResponsePayload.getExtension());

        if (chaincodeAction.getResponse().getStatus() >= 400) {
            throw new IllegalStateException(
                    "Chaincode response error: "
                            + chaincodeAction.getResponse().getMessage()
            );
        }

        List<FabricEndorsementInfo> result = new ArrayList<>();

        for (Endorsement endorsement : endorsedAction.getEndorsementsList()) {
            SerializedIdentity identity =
                    SerializedIdentity.parseFrom(endorsement.getEndorser());

            String mspId = identity.getMspid();

            String endorserCertificateBase64 =
                    Base64.getEncoder().encodeToString(
                            identity.getIdBytes().toByteArray()
                    );

            String proposalResponsePayloadBase64 =
                    Base64.getEncoder().encodeToString(
                            proposalResponsePayloadBytes.toByteArray()
                    );

            String signatureBase64 =
                    Base64.getEncoder().encodeToString(
                            endorsement.getSignature().toByteArray()
                    );

            result.add(
                    FabricEndorsementInfo.builder()
                            .mspId(mspId)
                            .endorserCertificateBase64(endorserCertificateBase64)
                            .proposalResponsePayloadBase64(proposalResponsePayloadBase64)
                            .signatureBase64(signatureBase64)
                            .build()
            );
        }

        return result;
    }

    private String sha256Hex(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder();

        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }
}