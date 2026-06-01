package com.cyphervault.iam.auth.infrastructure.persistence.outbox;

public enum OutboxStatus {
    PENDING,
    PUBLISHING,
    PUBLISHED,
    FAILED
}