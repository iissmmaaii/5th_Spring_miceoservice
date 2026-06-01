package com.cyphervault.fabric.userkey.infrastructure.messaging.kafka;

import com.cyphervault.fabric.userkey.application.RegisterUserPublicKeyOnFabricUseCase;import com.cyphervault.fabric.userkey.dto.UserPublicKeyRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserPublicKeyRegisteredKafkaConsumer {

    private final RegisterUserPublicKeyOnFabricUseCase  registerUserPublicKeyOnFabricUseCase;

    @KafkaListener(
            topics = "${app.kafka.topics.user-public-key-registered}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(
            UserPublicKeyRegisteredEvent event,
            Acknowledgment acknowledgment
    ) {
        try {
            log.info(
                    "KAFKA_USER_PUBLIC_KEY_EVENT_RECEIVED eventId={} userId={} keyId={}",
                    event.getEventId(),
                    event.getUserId(),
                    event.getKeyId()
            );

            registerUserPublicKeyOnFabricUseCase.execute(event);

            acknowledgment.acknowledge();

            log.info(
                    "KAFKA_USER_PUBLIC_KEY_EVENT_ACKED eventId={} userId={} keyId={}",
                    event.getEventId(),
                    event.getUserId(),
                    event.getKeyId()
            );

        } catch (Exception ex) {
            log.error(
                    "KAFKA_USER_PUBLIC_KEY_EVENT_PROCESSING_FAILED eventId={} userId={} keyId={}",
                    event != null ? event.getEventId() : null,
                    event != null ? event.getUserId() : null,
                    event != null ? event.getKeyId() : null,
                    ex
            );

            throw asRuntimeException(ex);
        }
    }

    private RuntimeException asRuntimeException(Exception ex) {
        if (ex instanceof RuntimeException runtimeException) {
            return runtimeException;
        }

        return new IllegalStateException("Kafka event processing failed", ex);
    }
}