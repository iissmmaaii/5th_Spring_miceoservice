package com.cyphervault.account_service.account.application;

import com.cyphervault.account_service.account.domain.gateway.FabricAccountProofGateway;
import com.cyphervault.account_service.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.account_service.account.dto.request.TransferMoneyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferMoneyUseCase {

    private final FabricAccountProofGateway fabricAccountProofGateway;

    public FabricProofEnvelope execute(TransferMoneyRequest request) {
        return fabricAccountProofGateway.transfer(request);
    }
}