package org.example.domain.auth.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.domain.auth.domain.code.AuthSuccessCode;
import org.example.domain.auth.presentation.dto.request.TokenRequest;
import org.example.domain.auth.presentation.dto.response.TokenResponse;
import org.example.domain.auth.service.AuthService;
import org.example.domain.user.presentation.dto.response.UserResponse;
import org.example.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "로그인 (Access Token + Refresh Token 발급)")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<TokenResponse>> login(
            @Valid @RequestBody TokenRequest request
    ) {
        TokenResponse tokens = authService.login(
                request.email(), request.password());

        return BaseResponse.success(AuthSuccessCode.LOGIN_SUCCESS, tokens);
    }

    @Operation(summary = "내 정보 조회 (Access Token 검증)")
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserResponse>> me(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("인증되지 않았습니다.");
        }

        Long userId = Long.parseLong(authentication.getName());
        UserResponse user = UserResponse.from(
                authService.getUserById(userId)
        );

        return BaseResponse.success(AuthSuccessCode.ME_READ_SUCCESS, user);
    }
}
