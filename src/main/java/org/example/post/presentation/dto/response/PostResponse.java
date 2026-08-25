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

    @Override
    public String toString() {
        return "[" + id + "] " + title + " - " + authorId + " (" + createdAt + ")\n" + content;
    }
}
