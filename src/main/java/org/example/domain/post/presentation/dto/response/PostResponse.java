package org.example.domain.post.presentation.dto.response;

import org.example.domain.post.domain.entity.Post;
import org.example.domain.post.domain.model.BoardType;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long userId,
        BoardType boardType
) {
    public PostResponse(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getId(),
                post.getBoardType()
        );
    }

    public static PostResponse from(Post post) {
        return new PostResponse(post);
    }
}
