package com.cyphervault.account_service.account.infrastructure.fabric;

import com.cyphervault.account_service.account.domain.gateway.FabricAccountProofGateway;
import com.cyphervault.account_service.account.dto.proof.FabricProofEnvelope;
import com.cyphervault.account_service.account.dto.request.BalanceProofRequest;
import com.cyphervault.account_service.account.dto.request.OpenAccountRequest;
import com.cyphervault.account_service.account.dto.request.TransferMoneyRequest;
import com.cyphervault.account_service.common.exception.AppException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class FabricOrchestratorAccountClient implements FabricAccountProofGateway {

    private final RestClient fabricOrchestratorRestClient;

    private static final ParameterizedTypeReference<OrchestratorApiResponse<FabricProofEnvelope>>
            FABRIC_PROOF_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    @Override
    @CircuitBreaker(
            name = "fabricOrchestrator",
            fallbackMethod = "openAccountFallback"
    )
    public FabricProofEnvelope openAccount(OpenAccountRequest request) {
        OrchestratorApiResponse<FabricProofEnvelope> response =
                fabricOrchestratorRestClient
                        .post()
                        .uri("/fabric/account/open-account-proof")
                        .body(request)
                        .retrieve()
                        .body(FABRIC_PROOF_RESPONSE_TYPE);

        return extractFabricProofEnvelope(response, "opening account");
    }

    @Override
    @CircuitBreaker(
            name = "fabricOrchestrator",
            fallbackMethod = "balanceFallback"
    )
    public FabricProofEnvelope getBalanceProof(BalanceProofRequest request) {
        OrchestratorApiResponse<FabricProofEnvelope> response =
                fabricOrchestratorRestClient
                        .post()
                        .uri("/fabric/account/balance-proof")
                        .body(request)
                        .retrieve()
                        .body(FABRIC_PROOF_RESPONSE_TYPE);

        return extractFabricProofEnvelope(response, "getting balance proof");
    }

    @Override
    @CircuitBreaker(
            name = "fabricOrchestrator",
            fallbackMethod = "transferFallback"
    )
    public FabricProofEnvelope transfer(TransferMoneyRequest request) {
        OrchestratorApiResponse<FabricProofEnvelope> response =
                fabricOrchestratorRestClient
                        .post()
                        .uri("/fabric/account/transfer-proof")
                        .body(request)
                        .retrieve()
                        .body(FABRIC_PROOF_RESPONSE_TYPE);

        return extractFabricProofEnvelope(response, "submitting transfer");
    }

    private FabricProofEnvelope extractFabricProofEnvelope(
            OrchestratorApiResponse<FabricProofEnvelope> response,
            String operation
    ) {
        if (response == null) {
            throw new AppException(
                    "Fabric Orchestrator returned empty response while " + operation,
                    HttpStatus.BAD_GATEWAY
            );
        }

        if (!Boolean.TRUE.equals(response.success())) {
            throw new AppException(
                    response.message() == null
                            ? "Fabric Orchestrator failed while " + operation
                            : response.message(),
                    HttpStatus.BAD_GATEWAY
            );
        }

        if (response.data() == null) {
            throw new AppException(
                    "Fabric Orchestrator returned empty proof data while " + operation,
                    HttpStatus.BAD_GATEWAY
            );
        }

        return response.data();
    }

    public FabricProofEnvelope openAccountFallback(
            OpenAccountRequest request,
            Throwable ex
    ) {
        throw new AppException(
                "Fabric Orchestrator is temporarily unavailable while opening account",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    public FabricProofEnvelope balanceFallback(
            BalanceProofRequest request,
            Throwable ex
    ) {
        throw new AppException(
                "Fabric Orchestrator is temporarily unavailable while getting balance proof",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    public FabricProofEnvelope transferFallback(
            TransferMoneyRequest request,
            Throwable ex
    ) {
        throw new AppException(
                "Fabric Orchestrator is temporarily unavailable while submitting transfer",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    public record OrchestratorApiResponse<T>(
            Boolean success,
            String message,
            T data
    ) {
    }
}
