package com.cyphervault.account_service.verification.infrastructure.persistence.adapter;

import com.cyphervault.account_service.verification.domain.model.UserVerification;
import com.cyphervault.account_service.verification.domain.repository.UserVerificationRepository;
import com.cyphervault.account_service.verification.infrastructure.persistence.entity.UserVerificationJpaEntity;
import com.cyphervault.account_service.verification.infrastructure.persistence.jpa.JpaUserVerificationStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserVerificationRepositoryAdapter implements UserVerificationRepository {

    private final JpaUserVerificationStatusRepository repository;

    @Override
    public UserVerification save(UserVerification verification) {
        UserVerificationJpaEntity saved = repository.save(toEntity(verification));
        return toDomain(saved);
    }

    @Override
    public Optional<UserVerification> findByUserId(UUID userId) {
        return repository.findById(userId).map(this::toDomain);
    }

    private UserVerificationJpaEntity toEntity(UserVerification domain) {
        return UserVerificationJpaEntity.builder()
                .userId(domain.getUserId())
                .status(domain.getStatus())
                .sourceFileId(domain.getSourceFileId())
                .rejectionReason(domain.getRejectionReason())
                .reviewedAt(domain.getReviewedAt())
                .reviewedByAdminUserId(domain.getReviewedByAdminUserId())
                .lastEventId(domain.getLastEventId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private UserVerification toDomain(UserVerificationJpaEntity entity) {
        return UserVerification.builder()
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .sourceFileId(entity.getSourceFileId())
                .rejectionReason(entity.getRejectionReason())
                .reviewedAt(entity.getReviewedAt())
                .reviewedByAdminUserId(entity.getReviewedByAdminUserId())
                .lastEventId(entity.getLastEventId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}