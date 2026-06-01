package com.cyphervault.iam.auth.domain.repository;

import com.cyphervault.iam.auth.domain.model.UserAccount;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository {

    UserAccount save(UserAccount user);

    Optional<UserAccount> findById(UUID userId);

    boolean existsByEmail(String email);
}