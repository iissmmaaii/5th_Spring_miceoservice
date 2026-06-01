package com.cyphervault.iam.auth.infrastructure.persistence.jpa;

import com.cyphervault.iam.auth.domain.enums.KeyStatus;
import com.cyphervault.iam.auth.infrastructure.persistence.entity.UserPublicKeyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserPublicKeyRepository
        extends JpaRepository<UserPublicKeyJpaEntity, UUID> {

    Optional<UserPublicKeyJpaEntity> findByUserUserIdAndKeyIdAndStatus(
            UUID userId,
            UUID keyId,
            KeyStatus status
    );

    boolean existsByFingerprint(String fingerprint);
}