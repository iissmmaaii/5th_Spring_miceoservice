package com.cyphervault.fabric.account.api;

import com.cyphervault.fabric.account.application.GetBalanceWithProofUseCase;
import com.cyphervault.fabric.account.application.OpenAccountWithProofUseCase;
import com.cyphervault.fabric.account.application.TransferWithProofUseCase;
import com.cyphervault.fabric.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.fabric.account.dto.request.BalanceProofRequest;
import com.cyphervault.fabric.account.dto.request.OpenAccountProofRequest;
import com.cyphervault.fabric.account.dto.request.TransferProofRequest;
import com.cyphervault.fabric.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fabric/account")
@RequiredArgsConstructor
public class AccountProofController {

    private final OpenAccountWithProofUseCase openAccountWithProofUseCase;
    private final GetBalanceWithProofUseCase getBalanceWithProofUseCase;
    private final TransferWithProofUseCase transferWithProofUseCase;

    @PostMapping("/open-account-proof")
    public ApiResponse<FabricProofEnvelope> openAccount(
            @Valid @RequestBody OpenAccountProofRequest request
    ) {
        FabricProofEnvelope proof = openAccountWithProofUseCase.execute(request);

        return ApiResponse.success(
                "Account opened with Fabric endorsement proof",
                proof
        );
    }

    @PostMapping("/balance-proof")
    public ApiResponse<FabricProofEnvelope> getBalanceProof(
            @Valid @RequestBody BalanceProofRequest request
    ) {
        FabricProofEnvelope proof = getBalanceWithProofUseCase.execute(request);

        return ApiResponse.success(
                "Balance proof retrieved with Fabric endorsements",
                proof
        );
    }

    @PostMapping("/transfer-proof")
    public ApiResponse<FabricProofEnvelope> transfer(
            @Valid @RequestBody TransferProofRequest request
    ) {
        FabricProofEnvelope proof = transferWithProofUseCase.execute(request);

        return ApiResponse.success(
                "Transfer submitted with Fabric endorsement proof",
                proof
        );
    }
}