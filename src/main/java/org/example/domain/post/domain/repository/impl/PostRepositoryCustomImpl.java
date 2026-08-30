package org.example.domain.post.domain.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.example.domain.post.domain.entity.Post;
import org.example.domain.post.domain.repository.PostRepositoryCustom;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.example.domain.post.domain.entity.QPost.post;
import static org.example.domain.user.domain.entity.QUser.user;

@Repository
public class PostRepositoryCustomImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public PostRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Post> searchByTitleAndUser(String keyword, String nickname) {
        return queryFactory
                .selectFrom(post)
                .join(post.user, user).fetchJoin()
                .where(
                        titleContains(keyword),
                        nicknameEquals(nickname)
                )
                .fetch();

    }

    private BooleanExpression titleContains(String keyword) {
        return StringUtils.hasText(keyword)
                ? post.title.contains(keyword)
                : null;
    }

    private BooleanExpression nicknameEquals(String nickname) {
        return StringUtils.hasText(nickname)
                ? user.nickname.contains(nickname)
                : null;
    }
}
