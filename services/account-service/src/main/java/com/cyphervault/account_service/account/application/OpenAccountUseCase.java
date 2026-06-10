package com.cyphervault.account_service.account.application;

import com.cyphervault.account_service.account.domain.gateway.FabricAccountProofGateway;
import com.cyphervault.account_service.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.account_service.account.dto.request.OpenAccountRequest;
import com.cyphervault.account_service.common.exception.BadRequestException;
import com.cyphervault.account_service.verification.application.VerificationGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OpenAccountUseCase {

    private final FabricAccountProofGateway fabricAccountProofGateway;
    private final VerificationGuard verificationGuard;

    public FabricProofEnvelope execute(UUID currentUserId, OpenAccountRequest request) {

        UUID ownerUserId = parseUuid(
                request.getOwnerUserId(),
                "Invalid owner user id"
        );

        if (!currentUserId.equals(ownerUserId)) {
            throw new BadRequestException("Owner user does not match authenticated user");
        }

        verificationGuard.ensureUserCanTransfer(ownerUserId);

        return fabricAccountProofGateway.openAccount(request);
    }

    private UUID parseUuid(String value, String errorMessage) {
        try {
            return UUID.fromString(value);
        } catch (Exception exception) {
            throw new BadRequestException(errorMessage);
        }
    }
}