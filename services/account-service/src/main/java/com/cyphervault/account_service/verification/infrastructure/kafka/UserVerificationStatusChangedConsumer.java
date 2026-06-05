package com.cyphervault.account_service.verification.infrastructure.kafka;

import com.cyphervault.account_service.verification.application.ApplyUserVerificationStatusChangedUseCase;
import com.cyphervault.account_service.verification.dto.UserVerificationStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserVerificationStatusChangedConsumer {

    private final ApplyUserVerificationStatusChangedUseCase useCase;

    @KafkaListener(
            topics = "${app.kafka.topics.user-verification-status-changed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ConsumerRecord<String, UserVerificationStatusChangedEvent> record) {
        UserVerificationStatusChangedEvent event = record.value();

        log.info(
                "USER_VERIFICATION_STATUS_EVENT_RECEIVED topic={} partition={} offset={} key={} eventId={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                event != null ? event.getEventId() : null
        );

        useCase.execute(
                event,
                record.topic(),
                record.key()
        );
    }
}