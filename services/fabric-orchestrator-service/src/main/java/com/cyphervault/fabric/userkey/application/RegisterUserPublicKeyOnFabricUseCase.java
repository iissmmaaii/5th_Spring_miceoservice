package com.cyphervault.fabric.userkey.application;

import com.cyphervault.fabric.common.exception.BadRequestException;
import com.cyphervault.fabric.userkey.domain.repository.IdentityRegistryRepository;
import com.cyphervault.fabric.userkey.dto.UserPublicKeyRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserPublicKeyOnFabricUseCase {

    private static final String EXPECTED_EVENT_TYPE = "IAM_USER_PUBLIC_KEY_REGISTERED";

    private final IdentityRegistryRepository identityRegistryRepository;

    public void execute(UserPublicKeyRegisteredEvent event) {
        validate(event);

        log.info(
                "FABRIC_USER_PUBLIC_KEY_REGISTRATION_STARTED eventId={} userId={} keyId={}",
                event.getEventId(),
                event.getUserId(),
                event.getKeyId()
        );

        identityRegistryRepository.registerUserPublicKey(event);

        log.info(
                "FABRIC_USER_PUBLIC_KEY_REGISTRATION_SUCCESS eventId={} userId={} keyId={}",
                event.getEventId(),
                event.getUserId(),
                event.getKeyId()
        );
    }

    private void validate(UserPublicKeyRegisteredEvent event) {
        if (event == null) {
            throw new BadRequestException("Kafka event body is required");
        }

        if (!EXPECTED_EVENT_TYPE.equals(event.getEventType())) {
            throw new BadRequestException("Unsupported event type: " + event.getEventType());
        }

        if (isBlank(event.getEventId())) {
            throw new BadRequestException("Event id is required");
        }

        if (isBlank(event.getUserId())) {
            throw new BadRequestException("User id is required");
        }

        if (isBlank(event.getKeyId())) {
            throw new BadRequestException("Key id is required");
        }

        if (isBlank(event.getPublicKeyHash())) {
            throw new BadRequestException("Public key hash is required");
        }

        if (isBlank(event.getPublicKeyPem())) {
            throw new BadRequestException("Public key PEM is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}