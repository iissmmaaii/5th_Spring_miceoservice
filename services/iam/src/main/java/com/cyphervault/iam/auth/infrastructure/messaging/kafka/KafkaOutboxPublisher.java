package com.cyphervault.iam.auth.infrastructure.messaging.kafka;

import com.cyphervault.iam.auth.infrastructure.persistence.entity.OutboxEventJpaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOutboxPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publish(OutboxEventJpaEntity event) {
        try {
            kafkaTemplate
                    .send(event.getTopic(), event.getKafkaKey(), event.getPayload())
                    .get(10, TimeUnit.SECONDS);

            log.info(
                    "OUTBOX_EVENT_PUBLISHED eventId={} topic={} key={}",
                    event.getEventId(),
                    event.getTopic(),
                    event.getKafkaKey()
            );

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka publishing interrupted", ex);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to publish outbox event to Kafka", ex);
        }
    }
}