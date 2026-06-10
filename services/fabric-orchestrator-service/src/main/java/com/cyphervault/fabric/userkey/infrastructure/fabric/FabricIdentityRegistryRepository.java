package com.cyphervault.fabric.userkey.infrastructure.fabric;

import com.cyphervault.fabric.common.exception.AppException;
import com.cyphervault.fabric.userkey.domain.repository.IdentityRegistryRepository;
import com.cyphervault.fabric.userkey.dto.UserPublicKeyRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.CommitStatusException;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.EndorseException;
import org.hyperledger.fabric.client.GatewayException;
import org.hyperledger.fabric.client.SubmitException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class FabricIdentityRegistryRepository implements IdentityRegistryRepository {

    private static final String DEFAULT_DEVICE_ID = "UNKNOWN_DEVICE";
    private static final String DEFAULT_ALGORITHM = "Ed25519";

    private final Contract identityRegistryContract;

    @Override
    public void registerUserPublicKey(UserPublicKeyRegisteredEvent event) {
        try {
            log.info(
                    "FABRIC_REGISTER_USER_PUBLIC_KEY_SUBMITTING eventId={} userId={} keyId={} deviceId={} algorithm={} publicKeyHash={} publicKeyPemLength={}",
                    event.getEventId(),
                    event.getUserId(),
                    event.getKeyId(),
                    DEFAULT_DEVICE_ID,
                    DEFAULT_ALGORITHM,
                    event.getPublicKeyHash(),
                    event.getPublicKeyPem() != null ? event.getPublicKeyPem().length() : null
            );

            byte[] result = identityRegistryContract.submitTransaction(
                    "RegisterUserPublicKey",
                    event.getUserId(),
                    event.getKeyId(),
                    DEFAULT_DEVICE_ID,
                    event.getPublicKeyHash(),
                    event.getPublicKeyPem(),
                    DEFAULT_ALGORITHM
            );

            log.info(
                    "FABRIC_CHAINCODE_TRANSACTION_SUBMITTED eventId={} userId={} keyId={} result={}",
                    event.getEventId(),
                    event.getUserId(),
                    event.getKeyId(),
                    new String(result, StandardCharsets.UTF_8)
            );

        } catch (
                EndorseException |
                SubmitException |
                CommitStatusException |
                CommitException ex
        ) {
            log.error(
                    "FABRIC_REGISTER_USER_PUBLIC_KEY_FAILED type={} message={} eventId={} userId={} keyId={}",
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    event.getEventId(),
                    event.getUserId(),
                    event.getKeyId(),
                    ex
            );

            throw new AppException(
                    "Failed to submit user public key event to Fabric: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );

        } catch (GatewayException ex) {
            log.error(
                    "FABRIC_GATEWAY_REGISTER_USER_PUBLIC_KEY_FAILED message={} eventId={} userId={} keyId={}",
                    ex.getMessage(),
                    event.getEventId(),
                    event.getUserId(),
                    event.getKeyId(),
                    ex
            );

            throw new AppException(
                    "Fabric gateway error while registering user public key: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}