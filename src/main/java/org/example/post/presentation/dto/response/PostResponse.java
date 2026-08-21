package org.example.post.presentation.dto.response;

import org.example.post.domain.Post;

public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        String createdAt
) {
    public PostResponse(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getCreatedAt()
        );
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " - " + author + " (" + createdAt + ")\n" + content;
    }
}