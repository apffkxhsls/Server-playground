package org.example.global.security.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.domain.auth.domain.repository.BlacklistedAccessTokenRepository;
import org.example.global.security.jwt.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final BlacklistedAccessTokenRepository blacklistedAccessTokenRepository;

    public JwtAuthFilter(
            JwtService jwtService,
            BlacklistedAccessTokenRepository blacklistedAccessTokenRepository
    ) {
        this.jwtService = jwtService;
        this.blacklistedAccessTokenRepository = blacklistedAccessTokenRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length()).trim();

            if (blacklistedAccessTokenRepository.existsByToken(token)) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "로그아웃된 토큰입니다."
                );
                return;
            }
            try {
                Long memberId = jwtService.verifyAndGetUserId(token);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        String.valueOf(memberId), null, Collections.emptyList());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (IllegalArgumentException | JWTVerificationException e) {
                // 유효하지 않은 토큰 또는 토큰이 없는 경우, 인증 없이 다음 필터로 넘겨요.
                // 여기서 예외를 던지지 않는 이유는, /v1/login 같이 인증이 필요 없는 API도
                // 이 필터를 거치기 때문이에요. 인증 여부 판단은 SecurityConfig의
                // authorizeHttpRequests 설정에서 담당합니다.
            }
        }

        filterChain.doFilter(request, response);
    }
}
