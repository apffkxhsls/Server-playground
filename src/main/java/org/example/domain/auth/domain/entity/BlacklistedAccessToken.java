package org.example.domain.auth.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class BlacklistedAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    protected BlacklistedAccessToken() {

    }

    public BlacklistedAccessToken(Long userId, String token, LocalDateTime expiresAt) {
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
