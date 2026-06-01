package com.cyphervault.iam.auth.domain.repository;

import com.cyphervault.iam.auth.domain.event.UserPublicKeyRegisteredEvent;

public interface UserPublicKeyEventPublisher {

    void publish(UserPublicKeyRegisteredEvent event);
}