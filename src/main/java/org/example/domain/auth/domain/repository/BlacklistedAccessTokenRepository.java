package org.example.domain.auth.domain.repository;

import org.example.domain.auth.domain.entity.BlacklistedAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedAccessTokenRepository extends JpaRepository<BlacklistedAccessToken, Long> {
    boolean existsByToken(String token);
}
