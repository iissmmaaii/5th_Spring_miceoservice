package com.cyphervault.fabric.account.infrastructure.fabric;

import com.cyphervault.fabric.account.domain.repository.AccountLedgerProofRepository;
import com.cyphervault.fabric.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.fabric.account.dto.request.BalanceProofRequest;
import com.cyphervault.fabric.account.dto.request.OpenAccountProofRequest;
import com.cyphervault.fabric.account.dto.request.TransferProofRequest;
import com.cyphervault.fabric.common.exception.AppException;
import com.cyphervault.fabric.config.FabricProperties;
import lombok.RequiredArgsConstructor;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Proposal;
import org.hyperledger.fabric.client.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FabricAccountLedgerProofRepository implements AccountLedgerProofRepository {

    private static final String CONTRACT_NAME = "AccountLedger";

    private final Contract accountLedgerContract;
    private final FabricProperties fabricProperties;
    private final FabricEndorsementProofFactory proofFactory;

    @Override
    public FabricProofEnvelope openAccount(OpenAccountProofRequest request) {
        try {
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

            return proofFactory.fromEndorsedTransaction(
                    endorsedTransaction,
                    fabricProperties.getChannelName(),
                    fabricProperties.getIdentityChaincodeName(),
                    CONTRACT_NAME,
                    "OpenPrimaryAccountWithUserSignature",
                    true,
                    "COMMITTED"
            );

        } catch (Exception ex) {
            throw new AppException(
                    "Failed to open account with Fabric endorsement proof",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public FabricProofEnvelope getBalanceProof(BalanceProofRequest request) {
        try {
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

            return proofFactory.fromEndorsedTransaction(
                    endorsedTransaction,
                    fabricProperties.getChannelName(),
                    fabricProperties.getIdentityChaincodeName(),
                    CONTRACT_NAME,
                    "GetBalanceProofWithUserSignature",
                    false,
                    "ENDORSED_ONLY_NOT_SUBMITTED"
            );

        } catch (Exception ex) {
            throw new AppException(
                    "Failed to get balance endorsement proof",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public FabricProofEnvelope transfer(TransferProofRequest request) {
        try {
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

            return proofFactory.fromEndorsedTransaction(
                    endorsedTransaction,
                    fabricProperties.getChannelName(),
                    fabricProperties.getIdentityChaincodeName(),
                    CONTRACT_NAME,
                    "TransferWithUserSignature",
                    true,
                    "COMMITTED"
            );

        } catch (Exception ex) {
            throw new AppException(
                    "Failed to transfer with Fabric endorsement proof",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private String[] endorsingOrgs() {
        return fabricProperties
                .getEndorsingOrgs()
                .toArray(new String[0]);
    }
}