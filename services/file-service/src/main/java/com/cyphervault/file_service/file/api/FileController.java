package com.cyphervault.file_service.file.api;

import com.cyphervault.file_service.common.context.CurrentUserHeaders;
import com.cyphervault.file_service.common.response.ApiResponse;
import com.cyphervault.file_service.file.application.DownloadFileUseCase;
import com.cyphervault.file_service.file.application.GetMyFilesUseCase;
import com.cyphervault.file_service.file.application.GetMyVerificationStatusUseCase;
import com.cyphervault.file_service.file.application.UploadFileUseCase;
import com.cyphervault.file_service.file.dto.response.FileStatusResponse;
import com.cyphervault.file_service.file.dto.response.FileUploadResponse;
import com.cyphervault.file_service.file.dto.response.VerificationStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final UploadFileUseCase uploadFileUseCase;
    private final GetMyFilesUseCase getMyFilesUseCase;
    private final GetMyVerificationStatusUseCase getMyVerificationStatusUseCase;
    private final DownloadFileUseCase downloadFileUseCase;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<FileUploadResponse> uploadFile(
            @RequestHeader(value = CurrentUserHeaders.USER_ID_HEADER, required = false)
            String userIdHeader,
            @RequestPart("file") MultipartFile file
    ) {
        UUID userId = CurrentUserHeaders.requiredUserId(userIdHeader);

        FileUploadResponse response = uploadFileUseCase.execute(userId, file);

        return ApiResponse.success(
                "File uploaded successfully and is pending admin review",
                response
        );
    }

    @GetMapping("/me")
    public ApiResponse<List<FileStatusResponse>> getMyFiles(
            @RequestHeader(value = CurrentUserHeaders.USER_ID_HEADER, required = false)
            String userIdHeader
    ) {
        UUID userId = CurrentUserHeaders.requiredUserId(userIdHeader);

        return ApiResponse.success(
                "User files retrieved successfully",
                getMyFilesUseCase.execute(userId)
        );
    }

    @GetMapping("/me/verification-status")
    public ApiResponse<VerificationStatusResponse> getMyVerificationStatus(
            @RequestHeader(value = CurrentUserHeaders.USER_ID_HEADER, required = false)
            String userIdHeader
    ) {
        UUID userId = CurrentUserHeaders.requiredUserId(userIdHeader);

        return ApiResponse.success(
                "Verification status retrieved successfully",
                getMyVerificationStatusUseCase.execute(userId)
        );
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadMyFile(
            @RequestHeader(value = CurrentUserHeaders.USER_ID_HEADER, required = false)
            String userIdHeader,
            @PathVariable UUID fileId
    ) {
        UUID userId = CurrentUserHeaders.requiredUserId(userIdHeader);

        DownloadFileUseCase.DownloadedFile downloaded =
                downloadFileUseCase.downloadForOwner(userId, fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloaded.file().getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + downloaded.file().getOriginalFilename() + "\""
                )
                .body(downloaded.resource());
    }
}