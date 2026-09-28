package org.example.global.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long accessTokenExpiresInSeconds;
    private final long refreshTokenExpiresInSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-expires-in-seconds:1800}") long accessTokenExpiresInSeconds,
            @Value("${security.jwt.refresh-token-expires-in-seconds:1209600}") long refreshTokenExpiresInSeconds
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.accessTokenExpiresInSeconds = accessTokenExpiresInSeconds;
        this.refreshTokenExpiresInSeconds = refreshTokenExpiresInSeconds;
    }

    public String generateAccessToken(Long userId, String email) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("email", email)
                .withClaim("tokenType", TokenType.ACCESS.name())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(accessTokenExpiresInSeconds)))
                .sign(algorithm);
    }

    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("tokenType", TokenType.REFRESH.name())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(refreshTokenExpiresInSeconds)))
                .sign(algorithm);
    }

    private Long verifyAndGetUserId(String token, TokenType expectedTokenType) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("토큰이 없습니다.");
        }

        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);

        String tokenType = jwt.getClaim("tokenType").asString();
        if (!expectedTokenType.name().equals(tokenType)) {
            throw new IllegalArgumentException("토큰 용도가 올바르지 않습니다.");
        }

        try {
            return Long.parseLong(jwt.getSubject());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("JWT의 회원 정보가 올바르지 않습니다.");
        }
    }

    public Long verifyAccessToken(String token) {
        return verifyAndGetUserId(token, TokenType.ACCESS);
    }

    public Long verifyRefreshToken(String token) {
        return verifyAndGetUserId(token, TokenType.REFRESH);
    }

    public LocalDateTime getExpiresAt(String token) {
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);

        return jwt.getExpiresAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
