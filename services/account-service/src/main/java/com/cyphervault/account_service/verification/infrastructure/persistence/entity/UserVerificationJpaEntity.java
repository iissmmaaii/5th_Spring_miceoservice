package com.cyphervault.account_service.verification.infrastructure.persistence.entity;

import com.cyphervault.account_service.verification.domain.enums.UserVerificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_verification_status")
public class UserVerificationJpaEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private UserVerificationStatus status;

    @Column(name = "source_file_id")
    private UUID sourceFileId;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "reviewed_by_admin_user_id")
    private UUID reviewedByAdminUserId;

    @Column(name = "last_event_id", nullable = false)
    private UUID lastEventId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}