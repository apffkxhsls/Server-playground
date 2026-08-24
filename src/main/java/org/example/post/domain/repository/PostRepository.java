package org.example.post.domain.repository;

import org.example.post.domain.model.Post;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository {

    Post save(Post post);

    Long generateId();

    List<Post> findAll();

    Optional<Post> findById(Long Id);

    void deleteById(Long Id);
}
