package com.cyphervault.file_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "file-storage")
public record FileStorageProperties(
        String rootDir,
        String publicDownloadBaseUrl,
        List<String> allowedContentTypes
) {
}