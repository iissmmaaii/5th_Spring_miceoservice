package com.cyphervault.file_service.file.dto.response;

import com.cyphervault.file_service.file.domain.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VerificationStatusResponse {

    private UUID userId;
    private VerificationStatus verificationStatus;
    private boolean canTransfer;
}