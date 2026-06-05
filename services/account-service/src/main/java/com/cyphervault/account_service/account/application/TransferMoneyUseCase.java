package com.cyphervault.account_service.account.application;

import com.cyphervault.account_service.account.domain.gateway.FabricAccountProofGateway;
import com.cyphervault.account_service.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.account_service.account.dto.request.TransferMoneyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.cyphervault.account_service.verification.application.VerificationGuard;
@Service
@RequiredArgsConstructor
public class TransferMoneyUseCase {

    private final FabricAccountProofGateway fabricAccountProofGateway;
    private final VerificationGuard verificationGuard;
    public FabricProofEnvelope execute(TransferMoneyRequest request) {
        verificationGuard.ensureUserCanTransfer(senderUserId)
        return fabricAccountProofGateway.transfer(request);
    }
}