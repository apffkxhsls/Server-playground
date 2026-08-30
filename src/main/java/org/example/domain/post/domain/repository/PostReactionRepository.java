package org.example.domain.post.domain.repository;

import org.example.domain.post.domain.entity.Post;
import org.example.domain.post.domain.entity.PostReaction;
import org.example.domain.post.presentation.dto.response.PostLikeCount;
import org.example.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    Optional<PostReaction> findByUserAndPost(User user, Post post);

    long countByPost(Post post);

    @Query("SELECT new org.example.domain.post.presentation.dto.response.PostLikeCount( r.post.id , COUNT(r.id) ) " +
            "FROM PostReaction r " +
            "WHERE r.post IN :posts GROUP BY r.post.id")
    List<PostLikeCount> findLikeCountsByPostIn(@Param("posts") List<Post> posts);
}
