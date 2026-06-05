package com.cyphervault.file_service.file.domain.model;

import com.cyphervault.file_service.file.domain.enums.FileReviewStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserUploadedFile {

    private UUID id;
    private UUID userId;

    private String fileUrl;
    private String originalFilename;
    private String storedFilename;
    private String storagePath;
    private String contentType;
    private long fileSizeBytes;

    private FileReviewStatus status;
    private String rejectionReason;

    private Instant uploadedAt;
    private Instant reviewedAt;
    private UUID reviewedByAdminUserId;

    public static UserUploadedFile createPending(
            UUID id,
            UUID userId,
            String fileUrl,
            String originalFilename,
            String storedFilename,
            String storagePath,
            String contentType,
            long fileSizeBytes
    ) {
        return UserUploadedFile.builder()
                .id(id)
                .userId(userId)
                .fileUrl(fileUrl)
                .originalFilename(originalFilename)
                .storedFilename(storedFilename)
                .storagePath(storagePath)
                .contentType(contentType)
                .fileSizeBytes(fileSizeBytes)
                .status(FileReviewStatus.PENDING)
                .uploadedAt(Instant.now())
                .build();
    }

    public void review(
            FileReviewStatus newStatus,
            UUID adminUserId,
            String rejectionReason
    ) {
        this.status = newStatus;
        this.reviewedByAdminUserId = adminUserId;
        this.reviewedAt = Instant.now();

        if (newStatus == FileReviewStatus.REJECTED) {
            this.rejectionReason = rejectionReason;
        } else {
            this.rejectionReason = null;
        }
    }
}