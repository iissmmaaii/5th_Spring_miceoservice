package com.cyphervault.file_service.file.application;

import com.cyphervault.file_service.file.domain.enums.FileReviewStatus;
import com.cyphervault.file_service.file.domain.repository.UserUploadedFileRepository;
import com.cyphervault.file_service.file.dto.response.FileStatusResponse;
import com.cyphervault.file_service.file.mapper.UserUploadedFileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminGetPendingFilesUseCase {

    private final UserUploadedFileRepository repository;
    private final UserUploadedFileMapper mapper;

    @Transactional(readOnly = true)
    public List<FileStatusResponse> execute() {
        return repository.findByStatusOrderByUploadedAtAsc(FileReviewStatus.PENDING)
                .stream()
                .map(mapper::toStatusResponse)
                .toList();
    }
}