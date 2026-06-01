package com.cyphervault.iam.auth.infrastructure.persistence.adapter;

import com.cyphervault.iam.auth.domain.event.UserPublicKeyRegisteredEvent;
import com.cyphervault.iam.auth.domain.repository.UserPublicKeyEventPublisher;
import com.cyphervault.iam.auth.infrastructure.persistence.entity.OutboxEventJpaEntity;
import com.cyphervault.iam.auth.infrastructure.persistence.jpa.JpaOutboxEventRepository;
import com.cyphervault.iam.auth.infrastructure.persistence.outbox.OutboxStatus;
import com.cyphervault.iam.common.exception.InternalServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxUserPublicKeyEventPublisher implements UserPublicKeyEventPublisher {

    private final JpaOutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.user-public-key-registered}")
    private String topicName;

    @Override
    public void publish(UserPublicKeyRegisteredEvent event) {
        try {
            Instant now = Instant.now();

            String payload = objectMapper.writeValueAsString(event);

            OutboxEventJpaEntity outboxEvent = OutboxEventJpaEntity.builder()
                    .id(UUID.randomUUID())
                    .eventId(event.eventId())
                    .aggregateType("USER")
                    .aggregateId(event.userId())
                    .eventType(event.eventType())
                    .eventVersion(event.eventVersion())
                    .topic(topicName)
                    .kafkaKey(event.userId())
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .attempts(0)
                    .nextAttemptAt(now)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException ex) {
            throw new InternalServerException(
                    "Failed to serialize user public key event",
                    ex
            );

        } catch (DataAccessException ex) {
            throw new InternalServerException(
                    "Failed to save user public key event to outbox",
                    ex
            );
        }
    }
}