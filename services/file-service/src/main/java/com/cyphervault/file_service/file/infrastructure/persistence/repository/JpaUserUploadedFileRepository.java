package com.cyphervault.file_service.file.infrastructure.persistence.repository;

import com.cyphervault.file_service.file.domain.enums.FileReviewStatus;
import com.cyphervault.file_service.file.domain.model.UserUploadedFile;
import com.cyphervault.file_service.file.domain.repository.UserUploadedFileRepository;
import com.cyphervault.file_service.file.infrastructure.persistence.mapper.UserUploadedFilePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserUploadedFileRepository implements UserUploadedFileRepository {

    private final SpringDataUserUploadedFileJpaRepository jpaRepository;
    private final UserUploadedFilePersistenceMapper mapper;

    @Override
    public UserUploadedFile save(UserUploadedFile file) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(file))
        );
    }

    @Override
    public Optional<UserUploadedFile> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserUploadedFile> findByIdAndUserId(UUID id, UUID userId) {
        return jpaRepository.findByIdAndUserId(id, userId)
                .map(mapper::toDomain);
    }

    @Override
    public List<UserUploadedFile> findByUserIdOrderByUploadedAtDesc(UUID userId) {
        return jpaRepository.findByUserIdOrderByUploadedAtDesc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<UserUploadedFile> findByStatusOrderByUploadedAtAsc(FileReviewStatus status) {
        return jpaRepository.findByStatusOrderByUploadedAtAsc(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndStatus(UUID userId, FileReviewStatus status) {
        return jpaRepository.existsByUserIdAndStatus(userId, status);
    }
}