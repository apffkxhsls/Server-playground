package org.example.domain.user.presentation.dto.response;

import org.example.domain.user.domain.entity.User;

public record UserResponse(
        Long id,
        String nickname,
        String email
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail()
        );
    }
}
