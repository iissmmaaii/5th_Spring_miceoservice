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

    private final Contract identityRegistryContract;

    @Override
    public void registerUserPublicKey(UserPublicKeyRegisteredEvent event) {
        try {
            byte[] result = identityRegistryContract.submitTransaction(
                    "RegisterUserPublicKey",
                    event.getEventId(),
                    event.getUserId(),
                    event.getKeyId(),
                    event.getPublicKeyHash(),
                    event.getPublicKeyPem()
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
            throw new AppException(
                    "Failed to submit user public key event to Fabric",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );

        } catch (GatewayException ex) {
            throw new AppException(
                    "Fabric gateway error while registering user public key",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}