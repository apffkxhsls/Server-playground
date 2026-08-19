package org.example.post.controller.dto.request;

public record UpdatePostRequest(String newTitle, String newContent) {
    public void validate() {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다!");
        }
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("내용은 필수입니다!");
        }
    }
}
