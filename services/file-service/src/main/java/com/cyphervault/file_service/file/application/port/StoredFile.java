package com.cyphervault.file_service.file.application.port;

public record StoredFile(
        String originalFilename,
        String storedFilename,
        String storagePath,
        String contentType,
        long fileSizeBytes,
        String fileUrl
) {
}