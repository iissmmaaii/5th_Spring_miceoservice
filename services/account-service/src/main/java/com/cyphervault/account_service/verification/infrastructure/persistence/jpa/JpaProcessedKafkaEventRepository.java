package com.cyphervault.account_service.verification.infrastructure.persistence.jpa;

import com.cyphervault.account_service.verification.infrastructure.persistence.entity.ProcessedKafkaEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaProcessedKafkaEventRepository
        extends JpaRepository<ProcessedKafkaEventJpaEntity, UUID> {
}