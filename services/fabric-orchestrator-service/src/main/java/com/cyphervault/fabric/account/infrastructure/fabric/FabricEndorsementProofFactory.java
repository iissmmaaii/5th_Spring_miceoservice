 package com.cyphervault.fabric.account.infrastructure.fabric;

import com.cyphervault.fabric.account.dto.proof.FabricEndorsementInfo;
import com.cyphervault.fabric.account.dto.proof.FabricProofEnvelope;
import com.google.protobuf.ByteString;
import org.hyperledger.fabric.client.Transaction;
import org.hyperledger.fabric.protos.common.Envelope;
import org.hyperledger.fabric.protos.common.Payload;
import org.hyperledger.fabric.protos.msp.SerializedIdentity;
import org.hyperledger.fabric.protos.peer.ChaincodeAction;
import org.hyperledger.fabric.protos.peer.ChaincodeActionPayload;
import org.hyperledger.fabric.protos.peer.ChaincodeEndorsedAction;
import org.hyperledger.fabric.protos.peer.Endorsement;
import org.hyperledger.fabric.protos.peer.ProposalResponsePayload;
import org.hyperledger.fabric.protos.peer.TransactionAction;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
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

            byte[] transactionBytes = endorsedTransaction.getBytes();

            Envelope transactionEnvelope =
                    extractTransactionEnvelope(transactionBytes);

            List<FabricEndorsementInfo> endorsements =
                    extractEndorsementsFromEnvelope(transactionEnvelope);

            String envelopeBase64 =
                    Base64.getEncoder().encodeToString(
                            transactionEnvelope.toByteArray()
                    );

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

    private Envelope extractTransactionEnvelope(byte[] transactionBytes) throws Exception {
        List<Exception> failures = new ArrayList<>();

        try {
            Envelope envelope = extractEnvelopeFromPreparedTransaction(transactionBytes);
            validateEnvelope(envelope);
            return envelope;
        } catch (Exception ex) {
            failures.add(ex);
        }

        try {
            Envelope envelope = Envelope.parseFrom(transactionBytes);
            validateEnvelope(envelope);
            return envelope;
        } catch (Exception ex) {
            failures.add(ex);
        }

        IllegalStateException error = new IllegalStateException(
                "Unsupported endorsed transaction bytes format. Tried PreparedTransaction and Envelope."
        );

        for (Exception failure : failures) {
            error.addSuppressed(failure);
        }

        throw error;
    }

    private Envelope extractEnvelopeFromPreparedTransaction(byte[] transactionBytes) throws Exception {
        Class<?> preparedTransactionClass =
                Class.forName("org.hyperledger.fabric.protos.gateway.PreparedTransaction");

        Method parseFromMethod =
                preparedTransactionClass.getMethod("parseFrom", byte[].class);

        Object preparedTransaction =
                parseFromMethod.invoke(null, transactionBytes);

        for (Method method : preparedTransactionClass.getMethods()) {
            if (method.getParameterCount() != 0) {
                continue;
            }

            Class<?> returnType = method.getReturnType();
            String methodName = method.getName().toLowerCase();

            if (Envelope.class.isAssignableFrom(returnType)) {
                Object value = method.invoke(preparedTransaction);

                if (value instanceof Envelope envelope) {
                    validateEnvelope(envelope);
                    return envelope;
                }
            }

            if (ByteString.class.isAssignableFrom(returnType)
                    && (methodName.contains("transaction")
                    || methodName.contains("envelope"))) {

                Object value = method.invoke(preparedTransaction);

                if (value instanceof ByteString byteString && !byteString.isEmpty()) {
                    Envelope envelope = Envelope.parseFrom(byteString.toByteArray());
                    validateEnvelope(envelope);
                    return envelope;
                }
            }
        }

        throw new IllegalStateException(
                "PreparedTransaction parsed, but no transaction envelope field was found"
        );
    }

    private void validateEnvelope(Envelope envelope) throws Exception {
        if (envelope == null) {
            throw new IllegalStateException("Envelope is null");
        }

        if (envelope.getPayload() == null || envelope.getPayload().isEmpty()) {
            throw new IllegalStateException("Envelope payload is empty");
        }

        Payload.parseFrom(envelope.getPayload());
    }

    private List<FabricEndorsementInfo> extractEndorsementsFromEnvelope(
            Envelope envelope
    ) throws Exception {

        Payload payload = Payload.parseFrom(
                envelope.getPayload()
        );

        org.hyperledger.fabric.protos.peer.Transaction tx =
                org.hyperledger.fabric.protos.peer.Transaction.parseFrom(
                        payload.getData()
                );

        if (tx.getActionsCount() == 0) {
            throw new IllegalStateException("Transaction has no actions");
        }

        TransactionAction transactionAction = tx.getActions(0);

        ChaincodeActionPayload chaincodeActionPayload =
                ChaincodeActionPayload.parseFrom(
                        transactionAction.getPayload()
                );

        ChaincodeEndorsedAction endorsedAction =
                chaincodeActionPayload.getAction();

        ByteString proposalResponsePayloadBytes =
                endorsedAction.getProposalResponsePayload();

        ProposalResponsePayload proposalResponsePayload =
                ProposalResponsePayload.parseFrom(
                        proposalResponsePayloadBytes
                );

        ChaincodeAction chaincodeAction =
                ChaincodeAction.parseFrom(
                        proposalResponsePayload.getExtension()
                );

        if (chaincodeAction.getResponse().getStatus() >= 400) {
            throw new IllegalStateException(
                    "Chaincode response error: "
                            + chaincodeAction.getResponse().getMessage()
            );
        }

        List<FabricEndorsementInfo> result = new ArrayList<>();

        String proposalResponsePayloadBase64 =
                Base64.getEncoder().encodeToString(
                        proposalResponsePayloadBytes.toByteArray()
                );

        for (Endorsement endorsement : endorsedAction.getEndorsementsList()) {
            SerializedIdentity identity =
                    SerializedIdentity.parseFrom(
                            endorsement.getEndorser()
                    );

            String mspId = identity.getMspid();

            String endorserCertificateBase64 =
                    Base64.getEncoder().encodeToString(
                            identity.getIdBytes().toByteArray()
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
