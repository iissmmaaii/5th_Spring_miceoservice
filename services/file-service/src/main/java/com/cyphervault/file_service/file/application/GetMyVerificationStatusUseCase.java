package com.cyphervault.file_service.file.application;

import com.cyphervault.file_service.file.domain.enums.FileReviewStatus;
import com.cyphervault.file_service.file.domain.enums.VerificationStatus;
import com.cyphervault.file_service.file.domain.repository.UserUploadedFileRepository;
import com.cyphervault.file_service.file.dto.response.VerificationStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMyVerificationStatusUseCase {

    private final UserUploadedFileRepository repository;

    @Transactional(readOnly = true)
    public VerificationStatusResponse execute(UUID userId) {
        VerificationStatus status;

        if (repository.existsByUserIdAndStatus(userId, FileReviewStatus.APPROVED)) {
            status = VerificationStatus.APPROVED;
        } else if (repository.existsByUserIdAndStatus(userId, FileReviewStatus.PENDING)) {
            status = VerificationStatus.PENDING;
        } else if (repository.existsByUserIdAndStatus(userId, FileReviewStatus.REJECTED)) {
            status = VerificationStatus.REJECTED;
        } else {
            status = VerificationStatus.NOT_SUBMITTED;
        }

        return VerificationStatusResponse.builder()
                .userId(userId)
                .verificationStatus(status)
                .canTransfer(status == VerificationStatus.APPROVED)
                .build();
    }
}