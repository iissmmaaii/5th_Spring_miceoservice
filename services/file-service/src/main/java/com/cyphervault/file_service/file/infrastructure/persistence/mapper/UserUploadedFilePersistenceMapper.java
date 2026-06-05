package com.cyphervault.file_service.file.infrastructure.persistence.mapper;

import com.cyphervault.file_service.file.domain.model.UserUploadedFile;
import com.cyphervault.file_service.file.infrastructure.persistence.entity.UserUploadedFileJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserUploadedFilePersistenceMapper {

    public UserUploadedFile toDomain(UserUploadedFileJpaEntity entity) {
        return UserUploadedFile.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .fileUrl(entity.getFileUrl())
                .originalFilename(entity.getOriginalFilename())
                .storedFilename(entity.getStoredFilename())
                .storagePath(entity.getStoragePath())
                .contentType(entity.getContentType())
                .fileSizeBytes(entity.getFileSizeBytes())
                .status(entity.getStatus())
                .rejectionReason(entity.getRejectionReason())
                .uploadedAt(entity.getUploadedAt())
                .reviewedAt(entity.getReviewedAt())
                .reviewedByAdminUserId(entity.getReviewedByAdminUserId())
                .build();
    }

    public UserUploadedFileJpaEntity toEntity(UserUploadedFile domain) {
        return UserUploadedFileJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .fileUrl(domain.getFileUrl())
                .originalFilename(domain.getOriginalFilename())
                .storedFilename(domain.getStoredFilename())
                .storagePath(domain.getStoragePath())
                .contentType(domain.getContentType())
                .fileSizeBytes(domain.getFileSizeBytes())
                .status(domain.getStatus())
                .rejectionReason(domain.getRejectionReason())
                .uploadedAt(domain.getUploadedAt())
                .reviewedAt(domain.getReviewedAt())
                .reviewedByAdminUserId(domain.getReviewedByAdminUserId())
                .build();
    }
}