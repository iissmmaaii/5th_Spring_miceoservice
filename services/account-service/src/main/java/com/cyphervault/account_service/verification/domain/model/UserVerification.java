package com.cyphervault.account_service.verification.domain.model;

import com.cyphervault.account_service.verification.domain.enums.UserVerificationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserVerification {

    private UUID userId;
    private UserVerificationStatus status;
    private UUID sourceFileId;
    private String rejectionReason;
    private Instant reviewedAt;
    private UUID reviewedByAdminUserId;
    private UUID lastEventId;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserVerification createFromEvent(
            UUID userId,
            UserVerificationStatus status,
            UUID sourceFileId,
            String rejectionReason,
            Instant reviewedAt,
            UUID reviewedByAdminUserId,
            UUID eventId
    ) {
        Instant now = Instant.now();

        return UserVerification.builder()
                .userId(userId)
                .status(status)
                .sourceFileId(sourceFileId)
                .rejectionReason(rejectionReason)
                .reviewedAt(reviewedAt)
                .reviewedByAdminUserId(reviewedByAdminUserId)
                .lastEventId(eventId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void applyEvent(
            UserVerificationStatus newStatus,
            UUID sourceFileId,
            String rejectionReason,
            Instant reviewedAt,
            UUID reviewedByAdminUserId,
            UUID eventId
    ) {
        this.status = newStatus;
        this.sourceFileId = sourceFileId;
        this.rejectionReason = rejectionReason;
        this.reviewedAt = reviewedAt;
        this.reviewedByAdminUserId = reviewedByAdminUserId;
        this.lastEventId = eventId;
        this.updatedAt = Instant.now();
    }
}