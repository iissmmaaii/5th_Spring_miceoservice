package com.cyphervault.iam.auth.infrastructure.persistence.jpa;

import com.cyphervault.iam.auth.infrastructure.persistence.entity.AuthChallengeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaAuthChallengeRepository
        extends JpaRepository<AuthChallengeJpaEntity, UUID> {
}