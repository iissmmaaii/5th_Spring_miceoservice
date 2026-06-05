package com.cyphervault.file_service.file.application.port;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileStoragePort {

    StoredFile store(UUID fileId, UUID userId, MultipartFile file);

    Resource load(String storagePath);
}