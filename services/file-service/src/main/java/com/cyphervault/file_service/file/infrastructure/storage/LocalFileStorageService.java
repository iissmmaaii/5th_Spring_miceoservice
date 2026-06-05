package com.cyphervault.file_service.file.infrastructure.storage;

import com.cyphervault.file_service.common.exception.BadRequestException;
import com.cyphervault.file_service.common.exception.NotFoundException;
import com.cyphervault.file_service.config.FileStorageProperties;
import com.cyphervault.file_service.file.application.port.FileStoragePort;
import com.cyphervault.file_service.file.application.port.StoredFile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStoragePort {

    private final FileStorageProperties properties;

    @Override
    public StoredFile store(UUID fileId, UUID userId, MultipartFile file) {
        validateFile(file);

        String originalFilename = cleanOriginalFilename(file);
        String extension = extensionOf(originalFilename);
        String storedFilename = fileId + extension;

        Path userDirectory = Path.of(properties.rootDir(), userId.toString())
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(userDirectory);

            Path target = userDirectory.resolve(storedFilename).normalize();

            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String storagePath = userId + "/" + storedFilename;

            String fileUrl = properties.publicDownloadBaseUrl()
                    + "/" + fileId
                    + "/download";

            return new StoredFile(
                    originalFilename,
                    storedFilename,
                    storagePath,
                    file.getContentType(),
                    file.getSize(),
                    fileUrl
            );

        } catch (Exception exception) {
            throw new BadRequestException("Failed to store uploaded file");
        }
    }

    @Override
    public Resource load(String storagePath) {
        try {
            Path path = Path.of(properties.rootDir(), storagePath)
                    .toAbsolutePath()
                    .normalize();

            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new NotFoundException("File content not found");
            }

            return resource;

        } catch (MalformedURLException exception) {
            throw new NotFoundException("File content not found");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file is required");
        }

        String contentType = file.getContentType();

        if (!StringUtils.hasText(contentType)) {
            throw new BadRequestException("File content type is required");
        }

        boolean allowed = properties.allowedContentTypes()
                .stream()
                .anyMatch(allowedType -> allowedType.equalsIgnoreCase(contentType));

        if (!allowed) {
            throw new BadRequestException("Unsupported file type: " + contentType);
        }
    }

    private String cleanOriginalFilename(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (!StringUtils.hasText(originalFilename)) {
            return "uploaded-file";
        }

        return Path.of(originalFilename).getFileName().toString();
    }

    private String extensionOf(String filename) {
        int dotIndex = filename.lastIndexOf(".");

        if (dotIndex < 0) {
            return "";
        }

        return filename.substring(dotIndex).toLowerCase(Locale.ROOT);
    }
}