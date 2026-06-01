package com.cyphervault.iam.auth.domain.event;

public record UserPublicKeyRegisteredEvent(
        String eventId,
        String eventType,
        String eventVersion,
        String correlationId,

        String userId,
        String keyId,

        String publicKeyHash,
        String publicKeyPem

) {
}