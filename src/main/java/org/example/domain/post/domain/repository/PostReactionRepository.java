package org.example.domain.post.domain.repository;

import org.example.domain.post.domain.entity.Post;
import org.example.domain.post.domain.entity.PostReaction;
import org.example.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    Optional<PostReaction> findByUserAndPost(User user, Post post);
    long countByPost(Post post);
}
