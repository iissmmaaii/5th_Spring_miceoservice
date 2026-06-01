package com.cyphervault.iam.auth.infrastructure.crypto;

import com.cyphervault.iam.common.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HexFormat;

@Slf4j
@Component
public class Ed25519SignatureVerifier implements SignatureVerifier {

    @Override
    public void validatePublicKey(String publicKeyPem) {
        parsePublicKey(publicKeyPem);
    }

    @Override
    public String fingerprint(String publicKeyPem) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(publicKeyPem.trim().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception ex) {
            throw new BadRequestException("Failed to generate public key fingerprint");
        }
    }

    @Override
    public boolean verify(
            String publicKeyPem,
            String message,
            String base64Signature
    ) {
        try {
            PublicKey publicKey = parsePublicKey(publicKeyPem);

            Signature signature = Signature.getInstance("Ed25519");
            signature.initVerify(publicKey);
            signature.update(message.getBytes(StandardCharsets.UTF_8));

            byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);

            boolean valid = signature.verify(signatureBytes);

            log.info("ED25519_SIGNATURE_VERIFICATION valid={}", valid);

            return valid;

        } catch (Exception ex) {
            log.warn("ED25519_SIGNATURE_VERIFICATION_FAILED error={}", ex.getMessage());
            return false;
        }
    }

    private PublicKey parsePublicKey(String pem) {
        try {
            byte[] encoded = extractKeyBytes(pem);

            return KeyFactory.getInstance("Ed25519")
                    .generatePublic(new X509EncodedKeySpec(encoded));

        } catch (Exception ex) {
            throw new BadRequestException("Invalid Ed25519 public key");
        }
    }

    private byte[] extractKeyBytes(String pem) {
        String normalized = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        return Base64.getDecoder().decode(normalized);
    }
}