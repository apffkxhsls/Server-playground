package org.example.domain.post.domain.repository;

import org.example.domain.post.domain.entity.Post;
import org.example.domain.post.domain.model.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p JOIN FETCH p.user")
    List<Post> findAllWithUser();

    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.boardType = :boardType")  // WHERE p.boardType = 전달받은 boardType
    List<Post> findAllByBoardTypeWithUser(@Param("boardType") BoardType boardType);
}
