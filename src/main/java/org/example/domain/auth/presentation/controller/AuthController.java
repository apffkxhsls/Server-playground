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

    /**
     * 이메일과 비밀번호를 검증하고 Access Token과 Refresh Token을 발급한다.
     *
     * @param request 이메일과 비밀번호를 담은 로그인 요청
     * @return 발급된 Access Token과 Refresh Token을 담은 공통 응답
     * @throws IllegalArgumentException 이메일 또는 비밀번호가 올바르지 않은 경우
     */
    @Operation(summary = "로그인 (Access Token + Refresh Token 발급)")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<TokenResponse>> login(
            @Valid @RequestBody TokenRequest request
    ) {
        TokenResponse tokens = authService.login(
                request.email(), request.password());

        return BaseResponse.success(AuthSuccessCode.LOGIN_SUCCESS, tokens);
    }


    /**
     * Refresh Token을 검증하고 새로운 Access Token과 Refresh Token을 발급한다.
     *
     * @param request 재발급에 사용할 Refresh Token을 담은 요청
     * @return 새로 발급된 Access Token과 Refresh Token을 담은 공통 응답
     * @throws IllegalArgumentException Refresh Token이 유효하지 않거나 만료된 경우
     */
    @Operation(summary = "토큰 재발급 (Refresh Token)")
    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<TokenResponse>> reissueToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenResponse tokens = authService.reissue(request.refreshToken());

        return BaseResponse.success(AuthSuccessCode.REISSUE_SUCCESS, tokens);
    }

    /**
     * 현재 사용자의 Refresh Token을 삭제하고 Access Token을 블랙리스트에 등록한다.
     *
     * @param authentication 현재 인증된 사용자 정보
     * @param authorizationHeader 현재 Access Token이 포함된 Authorization 헤더
     * @return 로그아웃 결과를 담은 공통 응답
     */
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

    /**
     * Access Token으로 인증된 사용자의 기본 정보를 조회한다.
     *
     * @param authentication 현재 인증된 사용자 정보
     * @return 사용자 ID, 닉네임, 이메일을 담은 공통 응답
     * @throws IllegalArgumentException 인증 정보가 없거나 사용자가 존재하지 않는 경우
     */
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
