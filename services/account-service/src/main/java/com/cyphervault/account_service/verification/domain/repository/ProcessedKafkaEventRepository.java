package com.cyphervault.account_service.verification.domain.repository;

import java.util.UUID;

public interface ProcessedKafkaEventRepository {

    boolean existsByEventId(UUID eventId);

    void saveProcessedEvent(UUID eventId, String topic, String kafkaKey);
}