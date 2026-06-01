package com.cyphervault.fabric.account.domain.repository;

import com.cyphervault.fabric.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.fabric.account.dto.request.BalanceProofRequest;
import com.cyphervault.fabric.account.dto.request.OpenAccountProofRequest;
import com.cyphervault.fabric.account.dto.request.TransferProofRequest;

public interface AccountLedgerProofRepository {

    FabricProofEnvelope openAccount(OpenAccountProofRequest request);

    FabricProofEnvelope getBalanceProof(BalanceProofRequest request);

    FabricProofEnvelope transfer(TransferProofRequest request);
}