package com.cyphervault.iam.auth.infrastructure.persistence.outbox;

import com.cyphervault.iam.auth.infrastructure.messaging.kafka.KafkaOutboxPublisher;
import com.cyphervault.iam.auth.infrastructure.persistence.entity.OutboxEventJpaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherJob {

    private final OutboxEventService outboxEventService;
    private final KafkaOutboxPublisher kafkaOutboxPublisher;

    @Value("${app.outbox.publisher.batch-size:50}")
    private int batchSize;

    @Value("${app.outbox.publisher.lock-timeout-seconds:60}")
    private long lockTimeoutSeconds;

    @Scheduled(
            initialDelayString = "${app.outbox.publisher.initial-delay-ms:5000}",
            fixedDelayString = "${app.outbox.publisher.fixed-delay-ms:3000}"
    )
    public void publishPendingEvents() {
        outboxEventService.recoverStalePublishingEvents(Duration.ofSeconds(lockTimeoutSeconds));

        for (int i = 0; i < batchSize; i++) {
            Optional<OutboxEventJpaEntity> optionalEvent = outboxEventService.reserveNextEvent();

            if (optionalEvent.isEmpty()) {
                return;
            }

            OutboxEventJpaEntity event = optionalEvent.get();

            try {
                kafkaOutboxPublisher.publish(event);
                outboxEventService.markPublished(event.getId());

            } catch (Exception ex) {
                log.error(
                        "OUTBOX_EVENT_PUBLISH_FAILED eventId={} topic={} attempts={}",
                        event.getEventId(),
                        event.getTopic(),
                        event.getAttempts(),
                        ex
                );

                outboxEventService.markFailed(event.getId(), ex);
            }
        }
    }
}