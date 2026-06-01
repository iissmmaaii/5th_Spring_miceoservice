package com.cyphervault.iam.auth.infrastructure.persistence.adapter;

import com.cyphervault.iam.auth.domain.enums.KeyStatus;
import com.cyphervault.iam.auth.domain.model.UserPublicKey;
import com.cyphervault.iam.auth.domain.repository.UserPublicKeyRepository;
import com.cyphervault.iam.auth.infrastructure.persistence.entity.UserAccountJpaEntity;
import com.cyphervault.iam.auth.infrastructure.persistence.mapper.UserPublicKeyPersistenceMapper;
import com.cyphervault.iam.auth.infrastructure.persistence.jpa.JpaUserAccountRepository;
import com.cyphervault.iam.auth.infrastructure.persistence.jpa.JpaUserPublicKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserPublicKeyRepositoryAdapter implements UserPublicKeyRepository {

    private final JpaUserPublicKeyRepository keyJpaRepository;
    private final JpaUserAccountRepository userJpaRepository;
    private final UserPublicKeyPersistenceMapper mapper;

    @Override
    public UserPublicKey save(UserPublicKey key) {
        UserAccountJpaEntity userRef =
                userJpaRepository.getReferenceById(key.getUserId());

        return mapper.toModel(
                keyJpaRepository.save(mapper.toEntity(key, userRef))
        );
    }

    @Override
    public Optional<UserPublicKey> findByUserIdAndKeyIdAndStatus(
            UUID userId,
            UUID keyId,
            KeyStatus status
    ) {
        return keyJpaRepository
                .findByUserUserIdAndKeyIdAndStatus(userId, keyId, status)
                .map(mapper::toModel);
    }

    @Override
    public boolean existsByFingerprint(String fingerprint) {
        return keyJpaRepository.existsByFingerprint(fingerprint);
    }
}