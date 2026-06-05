package com.cyphervault.account_service.verification.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processed_kafka_events")
public class ProcessedKafkaEventJpaEntity {

    @Id
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "kafka_key")
    private String kafkaKey;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
}