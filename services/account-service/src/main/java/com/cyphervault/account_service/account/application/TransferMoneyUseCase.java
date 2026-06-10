package com.cyphervault.account_service.account.application;

import com.cyphervault.account_service.account.domain.gateway.FabricAccountProofGateway;
import com.cyphervault.account_service.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.account_service.account.dto.request.TransferMoneyRequest;
import com.cyphervault.account_service.common.exception.BadRequestException;
import com.cyphervault.account_service.verification.application.VerificationGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferMoneyUseCase {

    private final FabricAccountProofGateway fabricAccountProofGateway;
    private final VerificationGuard verificationGuard;

    public FabricProofEnvelope execute(UUID currentUserId, TransferMoneyRequest request) {

        UUID senderUserId = parseUuid(
                request.getSenderUserId(),
                "Invalid sender user id"
        );

        if (!currentUserId.equals(senderUserId)) {
            throw new BadRequestException("Sender user does not match authenticated user");
        }

        verificationGuard.ensureUserCanTransfer(senderUserId);

        return fabricAccountProofGateway.transfer(request);
    }

    private UUID parseUuid(String value, String errorMessage) {
        try {
            return UUID.fromString(value);
        } catch (Exception exception) {
            throw new BadRequestException(errorMessage);
        }
    }
}