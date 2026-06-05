package com.cyphervault.file_service.file.application;

import com.cyphervault.file_service.common.exception.BadRequestException;
import com.cyphervault.file_service.common.exception.NotFoundException;
import com.cyphervault.file_service.file.domain.enums.FileReviewStatus;
import com.cyphervault.file_service.file.domain.model.UserUploadedFile;
import com.cyphervault.file_service.file.domain.repository.UserUploadedFileRepository;
import com.cyphervault.file_service.file.dto.event.UserVerificationStatusChangedEvent;
import com.cyphervault.file_service.file.dto.request.AdminUpdateFileStatusRequest;
import com.cyphervault.file_service.file.dto.response.FileStatusResponse;
import com.cyphervault.file_service.file.infrastructure.kafka.UserVerificationStatusEventPublisher;
import com.cyphervault.file_service.file.mapper.UserUploadedFileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUpdateFileStatusUseCase {

    private final UserUploadedFileRepository repository;
    private final UserUploadedFileMapper mapper;
    private final UserVerificationStatusEventPublisher eventPublisher;

    @Transactional
    public FileStatusResponse execute(
            UUID adminUserId,
            UUID fileId,
            AdminUpdateFileStatusRequest request
    ) {
        if (request.getStatus() == null) {
            throw new BadRequestException("File status is required");
        }

        if (request.getStatus() == FileReviewStatus.PENDING) {
            throw new BadRequestException("Admin can only approve or reject a file");
        }

        if (request.getStatus() == FileReviewStatus.REJECTED
                && !StringUtils.hasText(request.getRejectionReason())) {
            throw new BadRequestException("Rejection reason is required when rejecting a file");
        }

        UserUploadedFile uploadedFile = repository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("Uploaded file not found"));

        uploadedFile.review(
                request.getStatus(),
                adminUserId,
                request.getRejectionReason()
        );

        UserUploadedFile savedFile = repository.save(uploadedFile);

        UserVerificationStatusChangedEvent event =
                UserVerificationStatusChangedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType("USER_VERIFICATION_STATUS_CHANGED")
                        .schemaVersion(1)
                        .userId(savedFile.getUserId())
                        .fileId(savedFile.getId())
                        .status(savedFile.getStatus().name())
                        .rejectionReason(savedFile.getRejectionReason())
                        .reviewedByAdminUserId(savedFile.getReviewedByAdminUserId())
                        .reviewedAt(savedFile.getReviewedAt())
                        .occurredAt(Instant.now())
                        .build();

        eventPublisher.publish(event);

        return mapper.toStatusResponse(savedFile);
    }
}