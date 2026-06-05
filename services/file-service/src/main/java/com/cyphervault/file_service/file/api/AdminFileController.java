package com.cyphervault.file_service.file.api;

import com.cyphervault.file_service.common.context.CurrentUserHeaders;
import com.cyphervault.file_service.common.response.ApiResponse;
import com.cyphervault.file_service.file.application.AdminGetPendingFilesUseCase;
import com.cyphervault.file_service.file.application.AdminUpdateFileStatusUseCase;
import com.cyphervault.file_service.file.application.DownloadFileUseCase;
import com.cyphervault.file_service.file.dto.request.AdminUpdateFileStatusRequest;
import com.cyphervault.file_service.file.dto.response.FileStatusResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
public class AdminFileController {

    private final AdminGetPendingFilesUseCase getPendingFilesUseCase;
    private final AdminUpdateFileStatusUseCase updateFileStatusUseCase;
    private final DownloadFileUseCase downloadFileUseCase;

    @GetMapping("/pending")
    public ApiResponse<List<FileStatusResponse>> getPendingFiles() {
        return ApiResponse.success(
                "Pending files retrieved successfully",
                getPendingFilesUseCase.execute()
        );
    }

    @PatchMapping("/{fileId}/status")
    public ApiResponse<FileStatusResponse> updateFileStatus(
            @RequestHeader(value = CurrentUserHeaders.USER_ID_HEADER, required = false)
            String adminUserIdHeader,
            @PathVariable UUID fileId,
            @Valid @RequestBody AdminUpdateFileStatusRequest request
    ) {
        UUID adminUserId = CurrentUserHeaders.requiredUserId(adminUserIdHeader);

        FileStatusResponse response = updateFileStatusUseCase.execute(
                adminUserId,
                fileId,
                request
        );

        return ApiResponse.success(
                "File status updated successfully",
                response
        );
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFileForAdmin(
            @PathVariable UUID fileId
    ) {
        DownloadFileUseCase.DownloadedFile downloaded =
                downloadFileUseCase.downloadForAdmin(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloaded.file().getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + downloaded.file().getOriginalFilename() + "\""
                )
                .body(downloaded.resource());
    }
}