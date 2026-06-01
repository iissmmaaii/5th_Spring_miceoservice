package com.cyphervault.iam.auth.domain.model;

import com.cyphervault.iam.auth.domain.enums.KeyStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class UserPublicKey {

    private UUID keyId;
    private UUID userId;
    private String deviceId;
    private String publicKeyPem;
    private String fingerprint;
    private KeyStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant revokedAt;

    public boolean isActive() {
        return status == KeyStatus.ACTIVE;
    }

    public UserPublicKey revoke() {
        return this.toBuilder()
                .status(KeyStatus.REVOKED)
                .revokedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}