package com.cyphervault.fabric.userkey.domain.repository;

import com.cyphervault.fabric.userkey.dto.UserPublicKeyRegisteredEvent;

public interface IdentityRegistryRepository {

    void registerUserPublicKey(UserPublicKeyRegisteredEvent event);
}