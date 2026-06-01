package com.cyphervault.iam.auth.domain.model;

import com.cyphervault.iam.auth.domain.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class UserAccount {

    private UUID userId;
    private String fullName;
    private String email;
    private UserStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}