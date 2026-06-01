package com.cyphervault.iam.auth.domain.model;

import com.cyphervault.iam.auth.domain.enums.ChallengePurpose;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class AuthChallenge {

    private UUID challengeId;
    private UUID userId;
    private String nonce;
    private ChallengePurpose purpose;
    private Instant expiresAt;
    private boolean used;
    private Instant createdAt;
    private Instant usedAt;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public AuthChallenge markAsUsed() {
        return this.toBuilder()
                .used(true)
                .usedAt(Instant.now())
                .build();
    }
}