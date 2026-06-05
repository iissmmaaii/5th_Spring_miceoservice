package com.cyphervault.file_service.file.application;

import com.cyphervault.file_service.common.exception.NotFoundException;
import com.cyphervault.file_service.file.application.port.FileStoragePort;
import com.cyphervault.file_service.file.domain.model.UserUploadedFile;
import com.cyphervault.file_service.file.domain.repository.UserUploadedFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DownloadFileUseCase {

    private final UserUploadedFileRepository repository;
    private final FileStoragePort storagePort;

    @Transactional(readOnly = true)
    public DownloadedFile downloadForOwner(UUID userId, UUID fileId) {
        UserUploadedFile uploadedFile = repository.findByIdAndUserId(fileId, userId)
                .orElseThrow(() -> new NotFoundException("Uploaded file not found"));

        Resource resource = storagePort.load(uploadedFile.getStoragePath());

        return new DownloadedFile(uploadedFile, resource);
    }

    @Transactional(readOnly = true)
    public DownloadedFile downloadForAdmin(UUID fileId) {
        UserUploadedFile uploadedFile = repository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("Uploaded file not found"));

        Resource resource = storagePort.load(uploadedFile.getStoragePath());

        return new DownloadedFile(uploadedFile, resource);
    }

    public record DownloadedFile(
            UserUploadedFile file,
            Resource resource
    ) {
    }
}