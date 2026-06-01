package com.cyphervault.fabric.account.application;

import com.cyphervault.fabric.account.domain.repository.AccountLedgerProofRepository;
import com.cyphervault.fabric.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.fabric.account.dto.request.BalanceProofRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetBalanceWithProofUseCase {

    private final AccountLedgerProofRepository accountLedgerProofRepository;

    public FabricProofEnvelope execute(BalanceProofRequest request) {
        return accountLedgerProofRepository.getBalanceProof(request);
    }
}