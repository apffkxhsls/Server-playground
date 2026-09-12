package org.example.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        @NotBlank(message = "이메일은 필수입니다.")
        String email
) {
}
