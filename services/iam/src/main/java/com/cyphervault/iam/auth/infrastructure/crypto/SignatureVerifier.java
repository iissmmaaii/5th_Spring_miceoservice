package com.cyphervault.iam.auth.infrastructure.crypto;

public interface SignatureVerifier {

    void validatePublicKey(String publicKeyPem);

    String fingerprint(String publicKeyPem);

    boolean verify(
            String publicKeyPem,
            String message,
            String base64Signature
    );
}