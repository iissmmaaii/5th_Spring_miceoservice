package com.cyphervault.iam.auth.infrastructure.persistence.mapper;

import com.cyphervault.iam.auth.domain.model.UserPublicKey;
import com.cyphervault.iam.auth.infrastructure.persistence.entity.UserAccountJpaEntity;
import com.cyphervault.iam.auth.infrastructure.persistence.entity.UserPublicKeyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPublicKeyPersistenceMapper {

    public UserPublicKeyJpaEntity toEntity(
            UserPublicKey model,
            UserAccountJpaEntity userRef
    ) {
        return UserPublicKeyJpaEntity.builder()
                .keyId(model.getKeyId())
                .user(userRef)
                .deviceId(model.getDeviceId())
                .publicKeyPem(model.getPublicKeyPem())
                .fingerprint(model.getFingerprint())
                .status(model.getStatus())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .revokedAt(model.getRevokedAt())
                .build();
    }

    public UserPublicKey toModel(UserPublicKeyJpaEntity entity) {
        return UserPublicKey.builder()
                .keyId(entity.getKeyId())
                .userId(entity.getUser().getUserId())
                .deviceId(entity.getDeviceId())
                .publicKeyPem(entity.getPublicKeyPem())
                .fingerprint(entity.getFingerprint())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .revokedAt(entity.getRevokedAt())
                .build();
    }
}