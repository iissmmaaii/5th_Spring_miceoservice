package com.cyphervault.file_service.file.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
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