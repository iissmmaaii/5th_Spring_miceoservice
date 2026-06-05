package com.cyphervault.file_service.file.dto.response;

import com.cyphervault.file_service.file.domain.enums.FileReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class FileUploadResponse {

    private UUID fileId;
    private UUID userId;
    private String fileUrl;
    private String originalFilename;
    private String contentType;
    private long fileSizeBytes;
    private FileReviewStatus status;
    private Instant uploadedAt;
}