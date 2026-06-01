package com.cyphervault.iam.auth.application.usecase;

import com.cyphervault.iam.auth.domain.enums.KeyStatus;
import com.cyphervault.iam.auth.domain.model.AuthChallenge;
import com.cyphervault.iam.auth.domain.model.UserAccount;
import com.cyphervault.iam.auth.domain.model.UserPublicKey;
import com.cyphervault.iam.auth.domain.repository.AuthChallengeRepository;
import com.cyphervault.iam.auth.domain.repository.UserAccountRepository;
import com.cyphervault.iam.auth.domain.repository.UserPublicKeyRepository;
import com.cyphervault.iam.auth.dto.request.VerifySignatureRequest;
import com.cyphervault.iam.auth.dto.response.VerifySignatureResponse;
import com.cyphervault.iam.auth.infrastructure.crypto.SignatureVerifier;
import com.cyphervault.iam.common.exception.BadRequestException;
import com.cyphervault.iam.common.exception.NotFoundException;
import com.cyphervault.iam.common.logging.MdcKeys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifySignatureUseCase {

    private final AuthChallengeRepository authChallengeRepository;
    private final UserPublicKeyRepository userPublicKeyRepository;
    private final UserAccountRepository userAccountRepository;
    private final SignatureVerifier signatureVerifier;

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.access-token-expiration-minutes:60}")
    private long accessTokenExpirationMinutes;

    @Transactional
    public VerifySignatureResponse execute(VerifySignatureRequest request) {

        MDC.put(MdcKeys.USER_ID, request.getUserId().toString());
        MDC.put(MdcKeys.KEY_ID, request.getKeyId().toString());
        MDC.put(MdcKeys.CHALLENGE_ID, request.getChallengeId().toString());

        log.info(
                "VERIFY_SIGNATURE_STARTED userId={} keyId={} challengeId={}",
                request.getUserId(),
                request.getKeyId(),
                request.getChallengeId()
        );

        AuthChallenge challenge = authChallengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new NotFoundException("Challenge not found"));

        validateChallenge(request, challenge);

        UserPublicKey key = userPublicKeyRepository
                .findByUserIdAndKeyIdAndStatus(
                        request.getUserId(),
                        request.getKeyId(),
                        KeyStatus.ACTIVE
                )
                .orElseThrow(() -> new NotFoundException("Active public key not found"));

        String signedMessage = request.getChallengeId()
                + "."
                + request.getNonce();

        boolean signatureValid = signatureVerifier.verify(
                key.getPublicKeyPem(),
                signedMessage,
                request.getSignature()
        );

        if (!signatureValid) {
            log.warn(
                    "VERIFY_SIGNATURE_FAILED userId={} keyId={} challengeId={}",
                    request.getUserId(),
                    request.getKeyId(),
                    request.getChallengeId()
            );

            throw new BadRequestException("Invalid digital signature");
        }

        UserAccount user = userAccountRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User account not found"));

        String role = "USER";

        List<String> permissions = List.of(
                "IAM.Auth.Login",
                "IAM.Keys.View"
        );

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(accessTokenExpirationMinutes * 60);

        String accessToken = generateAccessToken(
                request.getUserId().toString(),
                user.getEmail(),
                role,
                permissions,
                now,
                expiresAt
        );

        authChallengeRepository.save(challenge.markAsUsed());

        log.info(
                "VERIFY_SIGNATURE_SUCCESS userId={} keyId={} challengeId={}",
                request.getUserId(),
                request.getKeyId(),
                request.getChallengeId()
        );

        return VerifySignatureResponse.builder()
                .accessToken(accessToken)
                .expiresAt(expiresAt)
                .userId(request.getUserId())
                .email(user.getEmail())
                .role(role)
                .permissions(permissions)
                .build();
    }

    private String generateAccessToken(
            String userId,
            String email,
            String role,
            List<String> permissions,
            Instant issuedAt,
            Instant expiresAt
    ) {
        SecretKey key = Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .claim("role", role)
                .claim("permissions", permissions)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    private void validateChallenge(
            VerifySignatureRequest request,
            AuthChallenge challenge
    ) {
        if (!challenge.getUserId().equals(request.getUserId())) {
            throw new BadRequestException("Invalid challenge");
        }

        if (!challenge.getNonce().equals(request.getNonce())) {
            throw new BadRequestException("Invalid challenge");
        }

        if (challenge.isUsed()) {
            throw new BadRequestException("Challenge is already used");
        }

        if (challenge.isExpired()) {
            throw new BadRequestException("Challenge is expired");
        }
    }
}