package com.cyphervault.iam.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifySignatureResponse {

    private String accessToken;


    private Instant expiresAt;

    private UUID userId;

    private String email;

    private String role;

    private List<String> permissions;
}