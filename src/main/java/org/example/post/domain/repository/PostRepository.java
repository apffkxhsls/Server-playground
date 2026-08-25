package org.example.post.domain.repository;

import org.example.post.domain.entity.Post;
import org.example.post.domain.model.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByBoardType(BoardType boardType);
}
