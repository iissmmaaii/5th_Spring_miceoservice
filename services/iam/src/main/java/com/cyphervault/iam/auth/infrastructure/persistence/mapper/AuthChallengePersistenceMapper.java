package com.cyphervault.iam.auth.infrastructure.persistence.mapper;

import com.cyphervault.iam.auth.domain.model.AuthChallenge;
import com.cyphervault.iam.auth.infrastructure.persistence.entity.AuthChallengeJpaEntity;
import com.cyphervault.iam.auth.infrastructure.persistence.entity.UserAccountJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthChallengePersistenceMapper {

    public AuthChallengeJpaEntity toEntity(
            AuthChallenge model,
            UserAccountJpaEntity userRef
    ) {
        return AuthChallengeJpaEntity.builder()
                .challengeId(model.getChallengeId())
                .user(userRef)
                .nonce(model.getNonce())
                .purpose(model.getPurpose())
                .expiresAt(model.getExpiresAt())
                .used(model.isUsed())
                .createdAt(model.getCreatedAt())
                .usedAt(model.getUsedAt())
                .build();
    }

    public AuthChallenge toModel(AuthChallengeJpaEntity entity) {
        return AuthChallenge.builder()
                .challengeId(entity.getChallengeId())
                .userId(entity.getUser().getUserId())
                .nonce(entity.getNonce())
                .purpose(entity.getPurpose())
                .expiresAt(entity.getExpiresAt())
                .used(entity.isUsed())
                .createdAt(entity.getCreatedAt())
                .usedAt(entity.getUsedAt())
                .build();
    }
}