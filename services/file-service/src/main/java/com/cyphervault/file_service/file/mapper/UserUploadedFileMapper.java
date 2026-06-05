package com.cyphervault.file_service.file.mapper;

import com.cyphervault.file_service.file.domain.model.UserUploadedFile;
import com.cyphervault.file_service.file.dto.response.FileStatusResponse;
import com.cyphervault.file_service.file.dto.response.FileUploadResponse;
import org.springframework.stereotype.Component;

@Component
public class UserUploadedFileMapper {

    public FileUploadResponse toUploadResponse(UserUploadedFile file) {
        return FileUploadResponse.builder()
                .fileId(file.getId())
                .userId(file.getUserId())
                .fileUrl(file.getFileUrl())
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSizeBytes(file.getFileSizeBytes())
                .status(file.getStatus())
                .uploadedAt(file.getUploadedAt())
                .build();
    }

    public FileStatusResponse toStatusResponse(UserUploadedFile file) {
        return FileStatusResponse.builder()
                .fileId(file.getId())
                .userId(file.getUserId())
                .fileUrl(file.getFileUrl())
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSizeBytes(file.getFileSizeBytes())
                .status(file.getStatus())
                .rejectionReason(file.getRejectionReason())
                .uploadedAt(file.getUploadedAt())
                .reviewedAt(file.getReviewedAt())
                .build();
    }
}