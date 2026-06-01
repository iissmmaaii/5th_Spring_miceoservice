package com.cyphervault.iam.auth.infrastructure.persistence.jpa;

import com.cyphervault.iam.auth.infrastructure.persistence.entity.UserAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaUserAccountRepository
        extends JpaRepository<UserAccountJpaEntity, UUID> {

    boolean existsByEmail(String email);
}