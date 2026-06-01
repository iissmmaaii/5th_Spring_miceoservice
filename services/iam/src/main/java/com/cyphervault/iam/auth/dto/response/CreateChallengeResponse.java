package com.cyphervault.iam.auth.dto.response;

import com.cyphervault.iam.auth.domain.enums.ChallengePurpose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChallengeResponse {

    private UUID challengeId;
    private String nonce;
    private long expiresInSeconds;
}