package com.cyphervault.file_service.file.infrastructure.persistence.repository;

import com.cyphervault.file_service.file.domain.enums.FileReviewStatus;
import com.cyphervault.file_service.file.infrastructure.persistence.entity.UserUploadedFileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserUploadedFileJpaRepository
        extends JpaRepository<UserUploadedFileJpaEntity, UUID> {

    List<UserUploadedFileJpaEntity> findByUserIdOrderByUploadedAtDesc(UUID userId);

    Optional<UserUploadedFileJpaEntity> findByIdAndUserId(UUID id, UUID userId);

    List<UserUploadedFileJpaEntity> findByStatusOrderByUploadedAtAsc(FileReviewStatus status);

    boolean existsByUserIdAndStatus(UUID userId, FileReviewStatus status);
}