package com.cyphervault.iam.auth.application.usecase;

import com.cyphervault.iam.auth.domain.enums.KeyStatus;
import com.cyphervault.iam.auth.domain.enums.UserStatus;
import com.cyphervault.iam.auth.domain.event.UserPublicKeyRegisteredEvent;
import com.cyphervault.iam.auth.domain.model.UserAccount;
import com.cyphervault.iam.auth.domain.model.UserPublicKey;
import com.cyphervault.iam.auth.domain.repository.UserAccountRepository;
import com.cyphervault.iam.auth.domain.repository.UserPublicKeyEventPublisher;
import com.cyphervault.iam.auth.domain.repository.UserPublicKeyRepository;
import com.cyphervault.iam.auth.dto.request.RegisterUserRequest;
import com.cyphervault.iam.auth.dto.response.RegisterUserResponse;
import com.cyphervault.iam.auth.infrastructure.crypto.SignatureVerifier;
import com.cyphervault.iam.common.exception.ConflictException;
import com.cyphervault.iam.common.logging.MdcKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private static final String EVENT_TYPE = "IAM_USER_PUBLIC_KEY_REGISTERED";
    private static final String EVENT_VERSION = "1";

    private final UserAccountRepository userAccountRepository;
    private final UserPublicKeyRepository userPublicKeyRepository;
    private final UserPublicKeyEventPublisher userPublicKeyEventPublisher;
    private final SignatureVerifier signatureVerifier;

    @Transactional
    public RegisterUserResponse execute(RegisterUserRequest request) {
        log.info("REGISTER_USER_STARTED email={}", request.getEmail());

        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already registered");
        }

        signatureVerifier.validatePublicKey(request.getPublicKeyPem());

        String publicKeyHash = signatureVerifier.fingerprint(request.getPublicKeyPem());

        if (userPublicKeyRepository.existsByFingerprint(publicKeyHash)) {
            throw new ConflictException("Public key is already registered");
        }

        UUID userId = UUID.randomUUID();
        UUID keyId = UUID.randomUUID();
        Instant now = Instant.now();

        MDC.put(MdcKeys.USER_ID, userId.toString());
        MDC.put(MdcKeys.KEY_ID, keyId.toString());

        try {
            UserAccount user = UserAccount.builder()
                    .userId(userId)
                    .fullName(request.getFullName())
                    .email(request.getEmail())
                    .status(UserStatus.ACTIVE)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            UserPublicKey key = UserPublicKey.builder()
                    .keyId(keyId)
                    .userId(userId)
                    .publicKeyPem(request.getPublicKeyPem())
                    .fingerprint(publicKeyHash)
                    .status(KeyStatus.ACTIVE)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            userAccountRepository.save(user);
            userPublicKeyRepository.save(key);

            UserPublicKeyRegisteredEvent event = new UserPublicKeyRegisteredEvent(
                    UUID.randomUUID().toString(),
                    EVENT_TYPE,
                    EVENT_VERSION,
                    MDC.get(MdcKeys.CORRELATION_ID),
                    userId.toString(),
                    keyId.toString(),
                    publicKeyHash,
                    request.getPublicKeyPem()
            );

            userPublicKeyEventPublisher.publish(event);

            log.info("REGISTER_USER_SUCCESS userId={} keyId={}", userId, keyId);

            return RegisterUserResponse.builder()
                    .userId(userId)
                    .keyId(keyId)
                    .status("PUBLIC_KEY_REGISTERED")
                    .build();

        } finally {
            MDC.remove(MdcKeys.USER_ID);
            MDC.remove(MdcKeys.KEY_ID);
        }
    }
}