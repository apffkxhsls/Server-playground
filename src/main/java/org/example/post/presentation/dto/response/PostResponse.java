package org.example.post.presentation.dto.response;

import org.example.post.domain.Post;
import org.example.post.domain.model.BoardType;

public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        BoardType boardType,
        String createdAt
) {
    public PostResponse(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getBoardType(),
                post.getCreatedAt()
        );
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " - " + author + " (" + createdAt + ")\n" + content;
    }
}
