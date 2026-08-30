package org.example.domain.post.domain.entity;

import jakarta.persistence.*;
import org.example.domain.user.domain.entity.User;
import org.example.global.entity.BaseTimeEntity;

@Entity
@Table(
        name = "post_reactions",
        uniqueConstraints = @UniqueConstraint(  // 같은 사용자가 같은 게시글에 좋아요를 두 번 누르면 DB 수준에서도 막아줌
                columnNames = {"post_id", "user_id"}
        ))
public class PostReaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // user : like = 1 : N
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    protected PostReaction() {
    }

    public PostReaction(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }

}
