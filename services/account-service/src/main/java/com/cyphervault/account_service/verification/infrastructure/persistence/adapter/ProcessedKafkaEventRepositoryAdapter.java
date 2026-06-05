package com.cyphervault.account_service.verification.infrastructure.persistence.adapter;

import com.cyphervault.account_service.verification.domain.repository.ProcessedKafkaEventRepository;
import com.cyphervault.account_service.verification.infrastructure.persistence.entity.ProcessedKafkaEventJpaEntity;
import com.cyphervault.account_service.verification.infrastructure.persistence.jpa.JpaProcessedKafkaEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProcessedKafkaEventRepositoryAdapter implements ProcessedKafkaEventRepository {

    private final JpaProcessedKafkaEventRepository repository;

    @Override
    public boolean existsByEventId(UUID eventId) {
        return repository.existsById(eventId);
    }

    @Override
    public void saveProcessedEvent(UUID eventId, String topic, String kafkaKey) {
        repository.save(
                ProcessedKafkaEventJpaEntity.builder()
                        .eventId(eventId)
                        .topic(topic)
                        .kafkaKey(kafkaKey)
                        .processedAt(Instant.now())
                        .build()
        );
    }
}