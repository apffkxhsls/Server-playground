package org.example.domain.post.presentation.dto.request;

public record UpdatePostRequest(String newTitle, String newContent) {
    public void validate() {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다!");
        }
    }
}
