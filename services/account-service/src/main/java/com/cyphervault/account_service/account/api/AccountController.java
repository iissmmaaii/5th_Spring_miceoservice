package com.cyphervault.account_service.account.api;

import com.cyphervault.account_service.account.application.GetBalanceProofUseCase;
import com.cyphervault.account_service.account.application.OpenAccountUseCase;
import com.cyphervault.account_service.account.application.TransferMoneyUseCase;
import com.cyphervault.account_service.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.account_service.account.dto.request.BalanceProofRequest;
import com.cyphervault.account_service.account.dto.request.OpenAccountRequest;
import com.cyphervault.account_service.account.dto.request.TransferMoneyRequest;
import com.cyphervault.account_service.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final OpenAccountUseCase openAccountUseCase;
    private final GetBalanceProofUseCase getBalanceProofUseCase;
    private final TransferMoneyUseCase transferMoneyUseCase;

    @PostMapping("/open")
    public ApiResponse<FabricProofEnvelope> openAccount(
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Valid @RequestBody OpenAccountRequest request
    ) {
        FabricProofEnvelope proof = openAccountUseCase.execute(currentUserId, request);

        return ApiResponse.success(
                "Account opened with Fabric proof",
                proof
        );
    }

    @PostMapping("/balance-proof")
    public ApiResponse<FabricProofEnvelope> getBalanceProof(
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Valid @RequestBody BalanceProofRequest request
    ) {
        FabricProofEnvelope proof = getBalanceProofUseCase.execute(currentUserId, request);

        return ApiResponse.success(
                "Balance proof retrieved successfully",
                proof
        );
    }

    @PostMapping("/transfer")
    public ApiResponse<FabricProofEnvelope> transfer(
            @RequestHeader("X-User-Id") UUID currentUserId,
            @Valid @RequestBody TransferMoneyRequest request
    ) {
        FabricProofEnvelope proof = transferMoneyUseCase.execute(currentUserId, request);

        return ApiResponse.success(
                "Transfer submitted with Fabric proof",
                proof
        );
    }
}