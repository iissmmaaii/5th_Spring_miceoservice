package com.cyphervault.iam.auth.infrastructure.persistence.adapter;

import com.cyphervault.iam.auth.domain.model.AuthChallenge;
import com.cyphervault.iam.auth.domain.repository.AuthChallengeRepository;
import com.cyphervault.iam.auth.infrastructure.persistence.entity.UserAccountJpaEntity;
import com.cyphervault.iam.auth.infrastructure.persistence.mapper.AuthChallengePersistenceMapper;
import com.cyphervault.iam.auth.infrastructure.persistence.jpa.JpaAuthChallengeRepository;
import com.cyphervault.iam.auth.infrastructure.persistence.jpa.JpaUserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AuthChallengeRepositoryAdapter implements AuthChallengeRepository {

    private final JpaAuthChallengeRepository challengeJpaRepository;
    private final JpaUserAccountRepository userJpaRepository;
    private final AuthChallengePersistenceMapper mapper;

    @Override
    public AuthChallenge save(AuthChallenge challenge) {
        UserAccountJpaEntity userRef =
                userJpaRepository.getReferenceById(challenge.getUserId());

        return mapper.toModel(
                challengeJpaRepository.save(mapper.toEntity(challenge, userRef))
        );
    }

    @Override
    public Optional<AuthChallenge> findById(UUID challengeId) {
        return challengeJpaRepository.findById(challengeId)
                .map(mapper::toModel);
    }
}