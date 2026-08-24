package org.example.post.instructure;

import org.example.post.domain.Post;
import org.example.post.domain.repository.PostRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryPostRepository implements PostRepository {

    private final List<Post> postList = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public synchronized Post save(Post post) {
        postList.add(post);
        return post;
    }

    @Override
    public synchronized Long generateId() {
        return nextId++;
    }

    @Override
    public synchronized List<Post> findAll() {
        return postList;
    }

    @Override
    public synchronized Optional<Post> findById(Long id) {
        return postList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public synchronized void deleteById(Long id) {
        postList.removeIf(post -> id.equals(post.getId()));
    }
}

