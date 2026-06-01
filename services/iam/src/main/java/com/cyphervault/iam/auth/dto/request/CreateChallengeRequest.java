package com.cyphervault.iam.auth.dto.request;

import com.cyphervault.iam.auth.domain.enums.ChallengePurpose;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateChallengeRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;


}