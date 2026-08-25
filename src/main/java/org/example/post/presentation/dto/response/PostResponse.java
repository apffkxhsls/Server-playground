package org.example.post.presentation.dto.response;

import org.example.post.domain.entity.Post;
import org.example.post.domain.model.BoardType;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        BoardType boardType,
        String createdAt
) {
    public PostResponse(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getId(),
                post.getBoardType(),
                post.getCreatedAt()
        );
    }

    public static PostResponse from(Post post) {
        return new PostResponse(post);
    }
}
