package com.cyphervault.fabric.account.application;

import com.cyphervault.fabric.account.domain.repository.AccountLedgerProofRepository;
import com.cyphervault.fabric.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.fabric.account.dto.request.TransferProofRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferWithProofUseCase {

    private final AccountLedgerProofRepository accountLedgerProofRepository;

    public FabricProofEnvelope execute(TransferProofRequest request) {
        return accountLedgerProofRepository.transfer(request);
    }
}