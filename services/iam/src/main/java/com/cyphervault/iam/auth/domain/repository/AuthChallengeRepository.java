package com.cyphervault.iam.auth.domain.repository;

import com.cyphervault.iam.auth.domain.model.AuthChallenge;

import java.util.Optional;
import java.util.UUID;

public interface AuthChallengeRepository {

    AuthChallenge save(AuthChallenge challenge);

    Optional<AuthChallenge> findById(UUID challengeId);
}