package org.example.domain.post.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.domain.post.domain.model.BoardType;

public record CreatePostRequest(
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 50, message = "제목은 50자를 초과할 수 없습니다.")
        String title,

        @Size(max = 500, message = "내용은 500자를 초과할 수 없습니다.")
        String content,

        @NotNull(message = "작성자 ID는 필수입니다.")
        Long userId,

        @NotNull(message = "게시판 선택은 필수입니다.")
        BoardType boardType
) {}
