package org.example.domain.auth.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.domain.auth.domain.code.AuthSuccessCode;
import org.example.domain.auth.presentation.dto.request.RefreshTokenRequest;
import org.example.domain.auth.presentation.dto.request.TokenRequest;
import org.example.domain.auth.presentation.dto.response.TokenResponse;
import org.example.domain.auth.service.AuthService;
import org.example.domain.user.presentation.dto.response.UserResponse;
import org.example.global.response.BaseResponse;
import org.springframework.http.HttpHeaders;
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

    @Operation(summary = "토큰 재발급 (Refresh Token)")
    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<TokenResponse>> reissueToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenResponse tokens = authService.reissue(request.refreshToken());

        return BaseResponse.success(AuthSuccessCode.REISSUE_SUCCESS, tokens);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logoutToken(
            Authentication authentication,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        Long userId = Long.parseLong(authentication.getName());
        String accessToken = authorizationHeader
                .substring("Bearer ".length())
                .trim();

        authService.logout(userId, accessToken);

        return BaseResponse.success(AuthSuccessCode.LOGOUT_SUCCESS, null);
    }
}
