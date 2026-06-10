 package com.cyphervault.fabric.account.infrastructure.fabric;

import com.cyphervault.fabric.account.domain.repository.AccountLedgerProofRepository;
import com.cyphervault.fabric.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.fabric.account.dto.request.BalanceProofRequest;
import com.cyphervault.fabric.account.dto.request.OpenAccountProofRequest;
import com.cyphervault.fabric.account.dto.request.TransferProofRequest;
import com.cyphervault.fabric.common.exception.AppException;
import com.cyphervault.fabric.config.FabricProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.EndorseException;
import org.hyperledger.fabric.client.Proposal;
import org.hyperledger.fabric.client.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
@Slf4j
public class FabricAccountLedgerProofRepository implements AccountLedgerProofRepository {

    private static final String CONTRACT_NAME = "AccountLedger";

    private final Contract accountLedgerContract;
    private final FabricProperties fabricProperties;
    private final FabricEndorsementProofFactory proofFactory;

    @Override
    public FabricProofEnvelope openAccount(OpenAccountProofRequest request) {
        try {
            log.info(
                    "FABRIC_OPEN_ACCOUNT_START clientRequestId={} ownerUserId={} ownerKeyId={} openingBalanceMinor={} currency={} nonce={} endorsingOrgs={}",
                    request.getClientRequestId(),
                    request.getOwnerUserId(),
                    request.getOwnerKeyId(),
                    request.getOpeningBalanceMinor(),
                    request.getCurrency(),
                    request.getNonce(),
                    fabricProperties.getEndorsingOrgs()
            );

            Proposal proposal = accountLedgerContract
                    .newProposal("OpenPrimaryAccountWithUserSignature")
                    .addArguments(
                            request.getClientRequestId(),
                            request.getOwnerUserId(),
                            request.getOwnerKeyId(),
                            request.getOpeningBalanceMinor(),
                            request.getCurrency(),
                            request.getNonce(),
                            request.getUserSignatureBase64()
                    )
                    .setEndorsingOrganizations(endorsingOrgs())
                    .build();

            Transaction endorsedTransaction = proposal.endorse();

            endorsedTransaction.submit();

            log.info(
                    "FABRIC_OPEN_ACCOUNT_SUCCESS clientRequestId={} ownerUserId={}",
                    request.getClientRequestId(),
                    request.getOwnerUserId()
            );

            return proofFactory.fromEndorsedTransaction(
                    endorsedTransaction,
                    fabricProperties.getChannelName(),
                    fabricProperties.getIdentityChaincodeName(),
                    CONTRACT_NAME,
                    "OpenPrimaryAccountWithUserSignature",
                    true,
                    "COMMITTED"
            );

        } catch (EndorseException ex) {
            log.error(
                    "FABRIC_OPEN_ACCOUNT_ENDORSE_FAILED clientRequestId={} ownerUserId={} ownerKeyId={} errorType={} errorMessage={}",
                    request.getClientRequestId(),
                    request.getOwnerUserId(),
                    request.getOwnerKeyId(),
                    ex.getClass().getName(),
                    ex.getMessage(),
                    ex
            );

            logGatewayExceptionDetails(ex);

            throw new AppException(
                    "Failed to open account with Fabric endorsement proof: "
                            + ex.getClass().getSimpleName()
                            + " - "
                            + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );

        } catch (Exception ex) {
            log.error(
                    "FABRIC_OPEN_ACCOUNT_FAILED clientRequestId={} ownerUserId={} ownerKeyId={} errorType={} errorMessage={}",
                    request.getClientRequestId(),
                    request.getOwnerUserId(),
                    request.getOwnerKeyId(),
                    ex.getClass().getName(),
                    ex.getMessage(),
                    ex
            );

            throw new AppException(
                    "Failed to open account with Fabric endorsement proof: "
                            + ex.getClass().getSimpleName()
                            + " - "
                            + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public FabricProofEnvelope getBalanceProof(BalanceProofRequest request) {
        try {
            log.info(
                    "FABRIC_BALANCE_PROOF_START clientRequestId={} userId={} keyId={} currency={} nonce={} endorsingOrgs={}",
                    request.getClientRequestId(),
                    request.getUserId(),
                    request.getKeyId(),
                    request.getCurrency(),
                    request.getNonce(),
                    fabricProperties.getEndorsingOrgs()
            );

            Proposal proposal = accountLedgerContract
                    .newProposal("GetBalanceProofWithUserSignature")
                    .addArguments(
                            request.getClientRequestId(),
                            request.getUserId(),
                            request.getKeyId(),
                            request.getCurrency(),
                            request.getNonce(),
                            request.getUserSignatureBase64()
                    )
                    .setEndorsingOrganizations(endorsingOrgs())
                    .build();

            Transaction endorsedTransaction = proposal.endorse();

            log.info(
                    "FABRIC_BALANCE_PROOF_SUCCESS clientRequestId={} userId={}",
                    request.getClientRequestId(),
                    request.getUserId()
            );

            return proofFactory.fromEndorsedTransaction(
                    endorsedTransaction,
                    fabricProperties.getChannelName(),
                    fabricProperties.getIdentityChaincodeName(),
                    CONTRACT_NAME,
                    "GetBalanceProofWithUserSignature",
                    false,
                    "ENDORSED_ONLY_NOT_SUBMITTED"
            );

        } catch (EndorseException ex) {
            log.error(
                    "FABRIC_BALANCE_PROOF_ENDORSE_FAILED clientRequestId={} userId={} keyId={} errorType={} errorMessage={}",
                    request.getClientRequestId(),
                    request.getUserId(),
                    request.getKeyId(),
                    ex.getClass().getName(),
                    ex.getMessage(),
                    ex
            );

            logGatewayExceptionDetails(ex);

            throw new AppException(
                    "Failed to get balance endorsement proof: "
                            + ex.getClass().getSimpleName()
                            + " - "
                            + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );

        } catch (Exception ex) {
            log.error(
                    "FABRIC_BALANCE_PROOF_FAILED clientRequestId={} userId={} keyId={} errorType={} errorMessage={}",
                    request.getClientRequestId(),
                    request.getUserId(),
                    request.getKeyId(),
                    ex.getClass().getName(),
                    ex.getMessage(),
                    ex
            );

            throw new AppException(
                    "Failed to get balance endorsement proof: "
                            + ex.getClass().getSimpleName()
                            + " - "
                            + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public FabricProofEnvelope transfer(TransferProofRequest request) {
        try {
            log.info(
                    "FABRIC_TRANSFER_START clientRequestId={} senderUserId={} senderKeyId={} receiverUserId={} amountMinor={} currency={} nonce={} endorsingOrgs={}",
                    request.getClientRequestId(),
                    request.getSenderUserId(),
                    request.getSenderKeyId(),
                    request.getReceiverUserId(),
                    request.getAmountMinor(),
                    request.getCurrency(),
                    request.getNonce(),
                    fabricProperties.getEndorsingOrgs()
            );

            Proposal proposal = accountLedgerContract
                    .newProposal("TransferWithUserSignature")
                    .addArguments(
                            request.getClientRequestId(),
                            request.getSenderUserId(),
                            request.getSenderKeyId(),
                            request.getReceiverUserId(),
                            request.getAmountMinor(),
                            request.getCurrency(),
                            request.getNonce(),
                            request.getUserSignatureBase64()
                    )
                    .setEndorsingOrganizations(endorsingOrgs())
                    .build();

            Transaction endorsedTransaction = proposal.endorse();

            endorsedTransaction.submit();

            log.info(
                    "FABRIC_TRANSFER_SUCCESS clientRequestId={} senderUserId={} receiverUserId={}",
                    request.getClientRequestId(),
                    request.getSenderUserId(),
                    request.getReceiverUserId()
            );

            return proofFactory.fromEndorsedTransaction(
                    endorsedTransaction,
                    fabricProperties.getChannelName(),
                    fabricProperties.getIdentityChaincodeName(),
                    CONTRACT_NAME,
                    "TransferWithUserSignature",
                    true,
                    "COMMITTED"
            );

        } catch (EndorseException ex) {
            log.error(
                    "FABRIC_TRANSFER_ENDORSE_FAILED clientRequestId={} senderUserId={} senderKeyId={} receiverUserId={} errorType={} errorMessage={}",
                    request.getClientRequestId(),
                    request.getSenderUserId(),
                    request.getSenderKeyId(),
                    request.getReceiverUserId(),
                    ex.getClass().getName(),
                    ex.getMessage(),
                    ex
            );

            logGatewayExceptionDetails(ex);

            throw new AppException(
                    "Failed to transfer with Fabric endorsement proof: "
                            + ex.getClass().getSimpleName()
                            + " - "
                            + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );

        } catch (Exception ex) {
            log.error(
                    "FABRIC_TRANSFER_FAILED clientRequestId={} senderUserId={} senderKeyId={} receiverUserId={} errorType={} errorMessage={}",
                    request.getClientRequestId(),
                    request.getSenderUserId(),
                    request.getSenderKeyId(),
                    request.getReceiverUserId(),
                    ex.getClass().getName(),
                    ex.getMessage(),
                    ex
            );

            throw new AppException(
                    "Failed to transfer with Fabric endorsement proof: "
                            + ex.getClass().getSimpleName()
                            + " - "
                            + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private void logGatewayExceptionDetails(Exception ex) {
        try {
            Method getDetailsMethod = ex.getClass().getMethod("getDetails");
            Object detailsObject = getDetailsMethod.invoke(ex);

            log.error("FABRIC_GATEWAY_EXCEPTION_DETAILS rawDetails={}", detailsObject);

            if (detailsObject instanceof Iterable<?> details) {
                for (Object detail : details) {
                    log.error("FABRIC_ENDORSE_DETAIL rawDetail={}", detail);
                    logDetailField(detail, "getAddress");
                    logDetailField(detail, "getMspId");
                    logDetailField(detail, "getMessage");
                }
            }

        } catch (NoSuchMethodException noDetailsMethod) {
            log.error("FABRIC_GATEWAY_EXCEPTION_NO_DETAILS_METHOD exceptionType={}", ex.getClass().getName());
        } catch (Exception reflectionError) {
            log.error(
                    "FABRIC_GATEWAY_EXCEPTION_DETAILS_READ_FAILED errorType={} errorMessage={}",
                    reflectionError.getClass().getName(),
                    reflectionError.getMessage(),
                    reflectionError
            );
        }

        Throwable cause = ex.getCause();
        while (cause != null) {
            log.error(
                    "FABRIC_EXCEPTION_CAUSE causeType={} causeMessage={}",
                    cause.getClass().getName(),
                    cause.getMessage(),
                    cause
            );
            cause = cause.getCause();
        }
    }

    private void logDetailField(Object detail, String methodName) {
        try {
            Method method = detail.getClass().getMethod(methodName);
            Object value = method.invoke(detail);
            log.error("FABRIC_ENDORSE_DETAIL_FIELD {}={}", methodName, value);
        } catch (Exception ignored) {
            // Some Fabric Gateway versions expose different detail fields.
        }
    }

    private String[] endorsingOrgs() {
        return fabricProperties
                .getEndorsingOrgs()
                .toArray(new String[0]);
    }
}
