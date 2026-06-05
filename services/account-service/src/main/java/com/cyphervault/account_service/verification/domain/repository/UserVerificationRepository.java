package com.cyphervault.account_service.verification.domain.repository;

import com.cyphervault.account_service.verification.domain.model.UserVerification;

import java.util.Optional;
import java.util.UUID;

public interface UserVerificationRepository {

    UserVerification save(UserVerification verification);

    Optional<UserVerification> findByUserId(UUID userId);
}