package com.cyphervault.file_service.file.infrastructure.kafka;

import com.cyphervault.file_service.file.dto.event.UserVerificationStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserVerificationStatusEventPublisher {

    private final KafkaTemplate<String, UserVerificationStatusChangedEvent> kafkaTemplate;

    @Value("${app.kafka.topics.user-verification-status-changed}")
    private String topic;

    public void publish(UserVerificationStatusChangedEvent event) {
        kafkaTemplate.send(
                topic,
                event.getUserId().toString(),
                event
        );
    }
}