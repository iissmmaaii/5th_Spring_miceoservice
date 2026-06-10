package com.cyphervault.account_service.account.application;

import com.cyphervault.account_service.account.domain.gateway.FabricAccountProofGateway;
import com.cyphervault.account_service.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.account_service.account.dto.request.BalanceProofRequest;
import com.cyphervault.account_service.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetBalanceProofUseCase {

    private final FabricAccountProofGateway fabricAccountProofGateway;

    public FabricProofEnvelope execute(UUID currentUserId, BalanceProofRequest request) {

        UUID requestedUserId = parseUuid(
                request.getUserId(),
                "Invalid user id"
        );

        if (!currentUserId.equals(requestedUserId)) {
            throw new BadRequestException("Requested user does not match authenticated user");
        }

        return fabricAccountProofGateway.getBalanceProof(request);
    }

    private UUID parseUuid(String value, String errorMessage) {
        try {
            return UUID.fromString(value);
        } catch (Exception exception) {
            throw new BadRequestException(errorMessage);
        }
    }
}