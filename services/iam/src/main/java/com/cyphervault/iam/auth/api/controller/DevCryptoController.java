package com.cyphervault.iam.auth.api.controller;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth/dev")
public class DevCryptoController {

    @GetMapping("/keypair")
    public ResponseEntity<KeyPairResponse> generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("Ed25519");
        KeyPair keyPair = generator.generateKeyPair();

        String publicKeyPem = toPem("PUBLIC KEY", keyPair.getPublic().getEncoded());

        String privateKeyBase64 = Base64.getEncoder()
                .encodeToString(keyPair.getPrivate().getEncoded());

        KeyPairResponse response = new KeyPairResponse();
        response.setPublicKeyPem(publicKeyPem);
        response.setPrivateKeyBase64(privateKeyBase64);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign")
    public ResponseEntity<SignResponse> sign(@RequestBody SignRequest request) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder()
                .decode(request.getPrivateKeyBase64());

        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519");

        PrivateKey privateKey = keyFactory.generatePrivate(
                new PKCS8EncodedKeySpec(privateKeyBytes)
        );

        String signedMessage = request.getChallengeId()
                + "."
                + request.getNonce();

        Signature signature = Signature.getInstance("Ed25519");
        signature.initSign(privateKey);
        signature.update(signedMessage.getBytes(StandardCharsets.UTF_8));

        String signatureBase64 = Base64.getEncoder()
                .encodeToString(signature.sign());

        SignResponse response = new SignResponse();
        response.setSignature(signatureBase64);
        response.setSignedMessage(signedMessage);

        return ResponseEntity.ok(response);
    }

    private String toPem(String type, byte[] encoded) {
        String base64 = Base64.getMimeEncoder(64, "\n".getBytes())
                .encodeToString(encoded);

        return "-----BEGIN " + type + "-----\n"
                + base64
                + "\n-----END " + type + "-----";
    }

    @Data
    public static class KeyPairResponse {
        private String publicKeyPem;
        private String privateKeyBase64;
    }

    @Data
    public static class SignRequest {
        private String privateKeyBase64;
        private String challengeId;
        private String nonce;
    }

    @Data
    public static class SignResponse {
        private String signature;
        private String signedMessage;
    }
}