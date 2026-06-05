package com.cyphervault.account_service.verification.application;

import com.cyphervault.account_service.common.exception.ForbiddenException;
import com.cyphervault.account_service.verification.domain.enums.UserVerificationStatus;
import com.cyphervault.account_service.verification.domain.repository.UserVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationGuard {

    private final UserVerificationRepository repository;

    public void ensureUserCanTransfer(UUID userId) {
        UserVerificationStatus status = repository.findByUserId(userId)
                .map(verification -> verification.getStatus())
                .orElse(UserVerificationStatus.NOT_SUBMITTED);

        if (status != UserVerificationStatus.APPROVED) {
            throw new ForbiddenException(
                    "User is not verified. Current verification status: " + status
            );
        }
    }
}