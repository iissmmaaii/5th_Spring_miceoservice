package com.cyphervault.iam.auth.infrastructure.persistence.adapter;

import com.cyphervault.iam.auth.domain.model.UserAccount;
import com.cyphervault.iam.auth.domain.repository.UserAccountRepository;
import com.cyphervault.iam.auth.infrastructure.persistence.mapper.UserAccountPersistenceMapper;
import com.cyphervault.iam.auth.infrastructure.persistence.jpa.JpaUserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserAccountRepositoryAdapter implements UserAccountRepository {

    private final JpaUserAccountRepository jpaRepository;
    private final UserAccountPersistenceMapper mapper;

    @Override
    public UserAccount save(UserAccount user) {
        return mapper.toModel(
                jpaRepository.save(mapper.toEntity(user))
        );
    }

    @Override
    public Optional<UserAccount> findById(UUID userId) {
        return jpaRepository.findById(userId)
                .map(mapper::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}