package com.cyphervault.file_service.file.dto.request;

import com.cyphervault.file_service.file.domain.enums.FileReviewStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUpdateFileStatusRequest {

    @NotNull
    private FileReviewStatus status;

    @Size(max = 500)
    private String rejectionReason;
}