package org.example.post.domain;

import org.example.post.domain.model.BoardType;

public record Post(
        Long id,          // 게시글 상세 화면 — 특정 게시글 식별용
        String title,     // 목록, 상세, 글쓰기 화면 — 제목
        String content,   // 목록(미리보기), 상세(전체) 화면 — 내용
        String author,    // 목록, 상세 화면 — 글쓴이
        BoardType boardType,
        String createdAt// 목록, 상세 화면 — 작성 시각
) {
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public BoardType getBoardType() {
        return boardType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Post update(String title, String content) {
        return new Post(id, title, content, author, boardType, createdAt);
    }
}
