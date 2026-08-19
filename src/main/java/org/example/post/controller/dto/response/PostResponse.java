package org.example.post.controller.dto.response;

import org.example.post.domain.Post;

public class PostResponse {
    Long id;
    String title;
    String content;
    String author;
    String createdAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getAuthor();
        this.createdAt = post.getCreatedAt();
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " - " + author + " (" + createdAt + ")\n" + content;
    }
}