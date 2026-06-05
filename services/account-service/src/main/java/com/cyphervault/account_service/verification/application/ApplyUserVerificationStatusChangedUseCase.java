package com.cyphervault.account_service.verification.application;

import com.cyphervault.account_service.common.exception.BadRequestException;
import com.cyphervault.account_service.verification.domain.enums.UserVerificationStatus;
import com.cyphervault.account_service.verification.domain.model.UserVerification;
import com.cyphervault.account_service.verification.domain.repository.ProcessedKafkaEventRepository;
import com.cyphervault.account_service.verification.domain.repository.UserVerificationRepository;
import com.cyphervault.account_service.verification.dto.UserVerificationStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplyUserVerificationStatusChangedUseCase {

    private final UserVerificationRepository verificationRepository;
    private final ProcessedKafkaEventRepository processedKafkaEventRepository;

    @Transactional
    public void execute(
            UserVerificationStatusChangedEvent event,
            String topic,
            String kafkaKey
    ) {
        if (event == null) {
            throw new BadRequestException("Kafka event body is required");
        }

        if (event.getEventId() == null) {
            throw new BadRequestException("Kafka eventId is required");
        }

        if (event.getUserId() == null) {
            throw new BadRequestException("Kafka userId is required");
        }

        if (processedKafkaEventRepository.existsByEventId(event.getEventId())) {
            return;
        }

        UserVerificationStatus status = parseStatus(event.getStatus());

        UserVerification verification = verificationRepository
                .findByUserId(event.getUserId())
                .orElseGet(() -> UserVerification.createFromEvent(
                        event.getUserId(),
                        status,
                        event.getFileId(),
                        event.getRejectionReason(),
                        event.getReviewedAt(),
                        event.getReviewedByAdminUserId(),
                        event.getEventId()
                ));

        verification.applyEvent(
                status,
                event.getFileId(),
                event.getRejectionReason(),
                event.getReviewedAt(),
                event.getReviewedByAdminUserId(),
                event.getEventId()
        );

        verificationRepository.save(verification);

        processedKafkaEventRepository.saveProcessedEvent(
                event.getEventId(),
                topic,
                kafkaKey
        );
    }

    private UserVerificationStatus parseStatus(String status) {
        try {
            return UserVerificationStatus.valueOf(status);
        } catch (Exception exception) {
            throw new BadRequestException("Invalid verification status: " + status);
        }
    }
}