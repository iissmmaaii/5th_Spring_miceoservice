package com.cyphervault.iam.auth.api.controller;

import com.cyphervault.iam.auth.application.usecase.CreateChallengeUseCase;
import com.cyphervault.iam.auth.application.usecase.RegisterUserUseCase;
import com.cyphervault.iam.auth.application.usecase.VerifySignatureUseCase;
import com.cyphervault.iam.auth.dto.request.CreateChallengeRequest;
import com.cyphervault.iam.auth.dto.request.RegisterUserRequest;
import com.cyphervault.iam.auth.dto.request.VerifySignatureRequest;
import com.cyphervault.iam.auth.dto.response.CreateChallengeResponse;
import com.cyphervault.iam.auth.dto.response.RegisterUserResponse;
import com.cyphervault.iam.auth.dto.response.VerifySignatureResponse;
import com.cyphervault.iam.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthCommandController {

    private final RegisterUserUseCase registerUserUseCase;
    private final CreateChallengeUseCase createChallengeUseCase;
    private final VerifySignatureUseCase verifySignatureUseCase;

    @PostMapping("/users/register")
    public ApiResponse<RegisterUserResponse> register(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        return ApiResponse.success(
                "User registered successfully",
                registerUserUseCase.execute(request)
        );
    }

    @PostMapping("/challenges")
    public ApiResponse<CreateChallengeResponse> createChallenge(
            @Valid @RequestBody CreateChallengeRequest request
    ) {
        return ApiResponse.success(
                "Challenge created successfully",
                createChallengeUseCase.execute(request)
        );
    }

    @PostMapping("/signatures/verify")
    public ApiResponse<VerifySignatureResponse> verifySignature(
            @Valid @RequestBody VerifySignatureRequest request
    ) {
        return ApiResponse.success(
                "Signature verification completed",
                verifySignatureUseCase.execute(request)
        );
    }
}