package com.cyphervault.fabric.userkey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicKeyRegisteredEvent {

    private String eventId;
    private String eventType;
    private String eventVersion;
    private String correlationId;

    private String userId;
    private String keyId;

    private String publicKeyHash;
    private String publicKeyPem;
}