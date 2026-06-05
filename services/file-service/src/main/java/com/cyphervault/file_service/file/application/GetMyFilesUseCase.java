package com.cyphervault.file_service.file.application;

import com.cyphervault.file_service.file.domain.repository.UserUploadedFileRepository;
import com.cyphervault.file_service.file.dto.response.FileStatusResponse;
import com.cyphervault.file_service.file.mapper.UserUploadedFileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMyFilesUseCase {

    private final UserUploadedFileRepository repository;
    private final UserUploadedFileMapper mapper;

    @Transactional(readOnly = true)
    public List<FileStatusResponse> execute(UUID userId) {
        return repository.findByUserIdOrderByUploadedAtDesc(userId)
                .stream()
                .map(mapper::toStatusResponse)
                .toList();
    }
}