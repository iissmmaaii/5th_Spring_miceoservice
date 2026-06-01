package com.cyphervault.iam.auth.domain.repository;

import com.cyphervault.iam.auth.domain.enums.KeyStatus;
import com.cyphervault.iam.auth.domain.model.UserPublicKey;

import java.util.Optional;
import java.util.UUID;

public interface UserPublicKeyRepository {

    UserPublicKey save(UserPublicKey key);

    Optional<UserPublicKey> findByUserIdAndKeyIdAndStatus(
            UUID userId,
            UUID keyId,
            KeyStatus status
    );

    boolean existsByFingerprint(String fingerprint);
}