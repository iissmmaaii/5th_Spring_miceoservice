package com.cyphervault.file_service.file.application;

import com.cyphervault.file_service.file.application.port.FileStoragePort;
import com.cyphervault.file_service.file.application.port.StoredFile;
import com.cyphervault.file_service.file.domain.model.UserUploadedFile;
import com.cyphervault.file_service.file.domain.repository.UserUploadedFileRepository;
import com.cyphervault.file_service.file.dto.response.FileUploadResponse;
import com.cyphervault.file_service.file.mapper.UserUploadedFileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadFileUseCase {

    private final UserUploadedFileRepository repository;
    private final FileStoragePort storagePort;
    private final UserUploadedFileMapper mapper;

    @Transactional
    public FileUploadResponse execute(UUID userId, MultipartFile multipartFile) {
        UUID fileId = UUID.randomUUID();

        StoredFile storedFile = storagePort.store(
                fileId,
                userId,
                multipartFile
        );

        UserUploadedFile uploadedFile = UserUploadedFile.createPending(
                fileId,
                userId,
                storedFile.fileUrl(),
                storedFile.originalFilename(),
                storedFile.storedFilename(),
                storedFile.storagePath(),
                storedFile.contentType(),
                storedFile.fileSizeBytes()
        );

        UserUploadedFile savedFile = repository.save(uploadedFile);

        return mapper.toUploadResponse(savedFile);
    }
}