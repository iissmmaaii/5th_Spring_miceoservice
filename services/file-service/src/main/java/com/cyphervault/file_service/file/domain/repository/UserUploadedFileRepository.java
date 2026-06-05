package com.cyphervault.file_service.file.domain.repository;

import com.cyphervault.file_service.file.domain.enums.FileReviewStatus;
import com.cyphervault.file_service.file.domain.model.UserUploadedFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserUploadedFileRepository {

    UserUploadedFile save(UserUploadedFile file);

    Optional<UserUploadedFile> findById(UUID id);

    Optional<UserUploadedFile> findByIdAndUserId(UUID id, UUID userId);

    List<UserUploadedFile> findByUserIdOrderByUploadedAtDesc(UUID userId);

    List<UserUploadedFile> findByStatusOrderByUploadedAtAsc(FileReviewStatus status);

    boolean existsByUserIdAndStatus(UUID userId, FileReviewStatus status);
}