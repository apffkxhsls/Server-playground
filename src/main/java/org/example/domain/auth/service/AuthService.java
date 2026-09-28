package org.example.domain.auth.service;

import org.example.domain.auth.domain.entity.BlacklistedAccessToken;
import org.example.domain.auth.domain.entity.RefreshToken;
import org.example.domain.auth.domain.repository.BlacklistedAccessTokenRepository;
import org.example.domain.auth.domain.repository.RefreshTokenRepository;
import org.example.domain.auth.presentation.dto.response.TokenResponse;
import org.example.domain.user.domain.entity.User;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.global.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedAccessTokenRepository blacklistedAccessTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            BlacklistedAccessTokenRepository blacklistedAccessTokenRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.blacklistedAccessTokenRepository = blacklistedAccessTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${security.jwt.refresh-token-expires-in-seconds:1209600}")
    private long refreshTokenExpiresInSeconds;

    public User loginWithCredentials(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        if (user.getPassword() == null
                || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

    // 로그인: 두 토큰 동시 발급
    @Transactional
    public TokenResponse login(String email, String password) {
        User user = loginWithCredentials(email, password);

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        // 기존 Refresh Token 삭제 후 새로 저장
        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(
                RefreshToken.of(user.getId(), refreshToken, refreshTokenExpiresInSeconds)
        );

        return TokenResponse.of(accessToken, refreshToken);
    }

    // 재발급: Refresh Token으로 Access Token 재발급
    @Transactional
    public TokenResponse reissue(String refreshTokenValue) {
        Long userId = jwtService.verifyRefreshToken(refreshTokenValue);

        RefreshToken storedRefreshToken = refreshTokenRepository
                .findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Refresh Token입니다."));

        if (storedRefreshToken.isExpired()) {
            throw new IllegalArgumentException("만료된 Refresh Token입니다.");
        }

        if (!storedRefreshToken.getUserId().equals(userId)) {
            throw new IllegalArgumentException("아이디가 일치하지 않습니다.");
        }

        User user = getUserById(userId);

        String newAccessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail()
        );

        String newRefreshToken = jwtService.generateRefreshToken(userId);

        storedRefreshToken.rotate(
                newRefreshToken,
                refreshTokenExpiresInSeconds
        );

        return TokenResponse.of(newAccessToken, newRefreshToken);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }

    // 로그아웃
    @Transactional
    public void logout(Long userId, String accessToken) {
        refreshTokenRepository.deleteByUserId(userId);
        LocalDateTime expiresAt = jwtService.getExpiresAt(accessToken);

        BlacklistedAccessToken blacklistedAccessToken = new BlacklistedAccessToken(userId, accessToken, expiresAt);

        blacklistedAccessTokenRepository.save(blacklistedAccessToken);
    }

    // 인증 후 토큰 발급
    @Transactional
    public TokenResponse loginWithGoogle(String email, String nickname) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        new User(nickname, null, email)
                ));

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        // 기존 Refresh Token 삭제 후 새로 저장
        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(
                RefreshToken.of(user.getId(), refreshToken, refreshTokenExpiresInSeconds)
        );

        return TokenResponse.of(accessToken, refreshToken);
    }
}
