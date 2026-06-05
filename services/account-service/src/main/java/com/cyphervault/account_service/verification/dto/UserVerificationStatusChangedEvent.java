package com.cyphervault.account_service.verification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVerificationStatusChangedEvent {

    private UUID eventId;
    private String eventType;
    private int schemaVersion;

    private UUID userId;
    private UUID fileId;

    private String status;
    private String rejectionReason;

    private UUID reviewedByAdminUserId;
    private Instant reviewedAt;
    private Instant occurredAt;
}