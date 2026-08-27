package org.example.post.domain.entity;

import jakarta.persistence.*;
import org.example.post.domain.model.BoardType;

@Entity  // "이 클래스를 DB 테이블과 매핑해요" — 영속성 컨텍스트가 이 클래스를 관리해요
public class Post {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;          // 게시글 상세 화면 — 특정 게시글 식별용

    private String title;     // 목록, 상세, 글쓰기 화면 — 제목

    private String content;   // 목록(미리보기), 상세(전체) 화면 — 내용

    @ManyToOne(fetch = FetchType.LAZY)  // user : Post = 1 : N
    @JoinColumn(name = "user_id")       // post 테이블에 user_id FK 컬럼 생성
    private User user;    // 목록, 상세 화면 — 글쓴이

    @Enumerated(EnumType.STRING)  // enum 문자열 저장을 지정하는 어노테이션
    private BoardType boardType;

    private String createdAt;  // 목록, 상세 화면 — 작성 시각

    protected Post() {}

    public Post(String title, String content, User user, BoardType boardType, String createdAt) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.boardType = boardType;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public BoardType getBoardType() {
        return boardType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
