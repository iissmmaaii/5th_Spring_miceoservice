package com.cyphervault.iam.auth.infrastructure.persistence.jpa;

import com.cyphervault.iam.auth.infrastructure.persistence.entity.OutboxEventJpaEntity;
import com.cyphervault.iam.auth.infrastructure.persistence.outbox.OutboxStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaOutboxEventRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OutboxEventJpaEntity> findFirstByStatusInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
            Collection<OutboxStatus> statuses,
            Instant now
    );

    List<OutboxEventJpaEntity> findTop50ByStatusAndLockedAtBeforeOrderByCreatedAtAsc(
            OutboxStatus status,
            Instant lockedAt
    );
}