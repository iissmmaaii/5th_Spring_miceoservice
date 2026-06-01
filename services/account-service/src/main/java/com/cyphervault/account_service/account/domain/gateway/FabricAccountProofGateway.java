package com.cyphervault.account_service.account.domain.gateway;

import com.cyphervault.account_service.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.account_service.account.dto.request.BalanceProofRequest;
import com.cyphervault.account_service.account.dto.request.OpenAccountRequest;
import com.cyphervault.account_service.account.dto.request.TransferMoneyRequest;

public interface FabricAccountProofGateway {

    FabricProofEnvelope openAccount(OpenAccountRequest request);

    FabricProofEnvelope getBalanceProof(BalanceProofRequest request);

    FabricProofEnvelope transfer(TransferMoneyRequest request);
}