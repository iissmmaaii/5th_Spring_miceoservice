package com.cyphervault.iam.auth.infrastructure.persistence.outbox;

import com.cyphervault.iam.auth.infrastructure.persistence.entity.OutboxEventJpaEntity;
import com.cyphervault.iam.auth.infrastructure.persistence.jpa.JpaOutboxEventRepository;
import com.cyphervault.iam.common.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private static final long RETRY_DELAY_MULTIPLIER_SECONDS = 5L;
    private static final long MAX_RETRY_DELAY_SECONDS = 300L;

    private final JpaOutboxEventRepository outboxEventRepository;

    @Transactional
    public Optional<OutboxEventJpaEntity> reserveNextEvent() {
        Instant now = Instant.now();

        Optional<OutboxEventJpaEntity> optionalEvent =
                outboxEventRepository.findFirstByStatusInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                        List.of(OutboxStatus.PENDING, OutboxStatus.FAILED),
                        now
                );

        if (optionalEvent.isEmpty()) {
            return Optional.empty();
        }

        OutboxEventJpaEntity event = optionalEvent.get();

        event.setStatus(OutboxStatus.PUBLISHING);
        event.setLockedAt(now);
        event.setAttempts(event.getAttempts() + 1);
        event.setUpdatedAt(now);

        return Optional.of(event);
    }

    @Transactional
    public void markPublished(UUID id) {
        OutboxEventJpaEntity event = findOutboxEventOrThrow(id);

        Instant now = Instant.now();

        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(now);
        event.setLockedAt(null);
        event.setLastError(null);
        event.setUpdatedAt(now);
    }

    @Transactional
    public void markFailed(UUID id, Exception ex) {
        OutboxEventJpaEntity event = findOutboxEventOrThrow(id);

        Instant now = Instant.now();

        long delaySeconds = calculateRetryDelaySeconds(event.getAttempts());

        event.setStatus(OutboxStatus.FAILED);
        event.setLockedAt(null);
        event.setNextAttemptAt(now.plusSeconds(delaySeconds));
        event.setLastError(resolveErrorMessage(ex));
        event.setUpdatedAt(now);
    }

    @Transactional
    public void recoverStalePublishingEvents(Duration lockTimeout) {
        Instant now = Instant.now();
        Instant lockedBefore = now.minus(lockTimeout);

        List<OutboxEventJpaEntity> staleEvents =
                outboxEventRepository.findTop50ByStatusAndLockedAtBeforeOrderByCreatedAtAsc(
                        OutboxStatus.PUBLISHING,
                        lockedBefore
                );

        for (OutboxEventJpaEntity event : staleEvents) {
            event.setStatus(OutboxStatus.FAILED);
            event.setLockedAt(null);
            event.setNextAttemptAt(now);
            event.setLastError("Recovered stale PUBLISHING event");
            event.setUpdatedAt(now);
        }
    }

    private OutboxEventJpaEntity findOutboxEventOrThrow(UUID id) {
        return outboxEventRepository.findById(id)
                .orElseThrow(() -> new InternalServerException(
                        "Outbox event not found: " + id
                ));
    }

    private long calculateRetryDelaySeconds(int attempts) {
        long delay = RETRY_DELAY_MULTIPLIER_SECONDS * attempts;
        return Math.min(delay, MAX_RETRY_DELAY_SECONDS);
    }

    private String resolveErrorMessage(Exception ex) {
        if (ex == null) {
            return "Unknown outbox publishing error";
        }

        if (ex.getMessage() == null || ex.getMessage().isBlank()) {
            return ex.getClass().getSimpleName();
        }

        return ex.getMessage();
    }
}