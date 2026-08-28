package org.example.domain.post.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record PostReactionRequest(
        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId
) { }
