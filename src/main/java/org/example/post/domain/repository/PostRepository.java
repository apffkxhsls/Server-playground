package org.example.post.domain.repository;

import org.example.post.domain.Post;
import org.example.post.domain.model.BoardType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository {

    Post save(Post post);

    Long generateId();

    List<Post> findAll();

    List<Post> findAllByBoardType(BoardType boardType);

    Optional<Post> findById(Long Id);

    void deleteById(Long Id);
}
