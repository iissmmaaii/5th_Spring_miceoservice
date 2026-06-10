package com.cyphervault.fabric.userkey.infrastructure.messaging.kafka;

import com.cyphervault.fabric.userkey.application.RegisterUserPublicKeyOnFabricUseCase;
import com.cyphervault.fabric.userkey.dto.UserPublicKeyRegisteredEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserPublicKeyRegisteredKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final RegisterUserPublicKeyOnFabricUseCase registerUserPublicKeyOnFabricUseCase;

    @KafkaListener(
            topics = "${app.kafka.topics.user-public-key-registered}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(
            String payload,
            Acknowledgment acknowledgment
    ) {
        UserPublicKeyRegisteredEvent event = null;

        try {
            String normalizedPayload = normalizePayload(payload);

            event = objectMapper.readValue(
                    normalizedPayload,
                    UserPublicKeyRegisteredEvent.class
            );

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
                    "KAFKA_USER_PUBLIC_KEY_EVENT_PROCESSING_FAILED eventId={} userId={} keyId={} payload={}",
                    event != null ? event.getEventId() : null,
                    event != null ? event.getUserId() : null,
                    event != null ? event.getKeyId() : null,
                    payload,
                    ex
            );

            throw asRuntimeException(ex);
        }
    }

    private String normalizePayload(String payload) throws JsonProcessingException {
        String trimmed = payload.trim();

        // Handles old/double-serialized messages like:
        // "{\"eventId\":\"...\",\"userId\":\"...\"}"
        if (trimmed.startsWith("\"")) {
            return objectMapper.readValue(trimmed, String.class);
        }

        // Handles normal JSON messages like:
        // {"eventId":"...","userId":"..."}
        return trimmed;
    }

    private RuntimeException asRuntimeException(Exception ex) {
        if (ex instanceof RuntimeException runtimeException) {
            return runtimeException;
        }

        return new IllegalStateException("Kafka event processing failed", ex);
    }
}