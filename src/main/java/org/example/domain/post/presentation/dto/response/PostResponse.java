package org.example.domain.post.presentation.dto.response;

import org.example.domain.post.domain.entity.Post;
import org.example.domain.post.domain.model.BoardType;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long userId,
        String nickname,
        BoardType boardType,
        Long likeCount
) {
    public PostResponse(Post post, Long likeCount) {
        this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getBoardType(),
                likeCount
        );
    }

    public static PostResponse from(Post post, Long likeCount) {
        return new PostResponse(post, likeCount);
    }
}
