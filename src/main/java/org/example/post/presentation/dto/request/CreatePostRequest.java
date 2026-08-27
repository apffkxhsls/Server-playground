package org.example.post.presentation.dto.request;

import org.example.post.domain.model.BoardType;

public record CreatePostRequest(
        String title,
        String content,
        Long userId,
        BoardType boardType
) {
    public void validate() {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다!");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 필수입니다!");
        }
        if (userId == null) {
            throw new IllegalArgumentException("작성자 ID는 필수입니다!");
        }
        if (boardType == null) {
            throw new IllegalArgumentException("게시판 선택은 필수입니다!");
        }
    }
}
