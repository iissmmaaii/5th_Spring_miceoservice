package com.cyphervault.iam.auth.application.usecase;

import com.cyphervault.iam.auth.domain.model.AuthChallenge;
import com.cyphervault.iam.auth.domain.repository.AuthChallengeRepository;
import com.cyphervault.iam.auth.domain.repository.UserAccountRepository;
import com.cyphervault.iam.auth.dto.request.CreateChallengeRequest;
import com.cyphervault.iam.auth.dto.response.CreateChallengeResponse;
import com.cyphervault.iam.common.exception.NotFoundException;
import com.cyphervault.iam.common.logging.MdcKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateChallengeUseCase {

    private static final long EXPIRES_IN_SECONDS = 120;

    private final UserAccountRepository userAccountRepository;
    private final AuthChallengeRepository authChallengeRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public CreateChallengeResponse execute(CreateChallengeRequest request) {
        MDC.put(MdcKeys.USER_ID, request.getUserId().toString());

        userAccountRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        UUID challengeId = UUID.randomUUID();
        Instant now = Instant.now();

        MDC.put(MdcKeys.CHALLENGE_ID, challengeId.toString());

        AuthChallenge challenge = AuthChallenge.builder()
                .challengeId(challengeId)
                .userId(request.getUserId())
                .nonce(generateNonce())
                .expiresAt(now.plusSeconds(EXPIRES_IN_SECONDS))
                .used(false)
                .createdAt(now)
                .build();

        AuthChallenge saved = authChallengeRepository.save(challenge);

        log.info("CREATE_CHALLENGE_SUCCESS userId={} challengeId={}",
                request.getUserId(), saved.getChallengeId());

        return CreateChallengeResponse.builder()
                .challengeId(saved.getChallengeId())
                .nonce(saved.getNonce())
                .expiresInSeconds(EXPIRES_IN_SECONDS)
                .build();
    }

    private String generateNonce() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}