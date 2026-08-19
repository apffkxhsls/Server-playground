package org.example.post.repository;

import org.example.post.domain.Post;

import java.util.ArrayList;
import java.util.List;

public class PostRepository {
    private final List<Post> postList = new ArrayList<>();
    private Long nextId = 1L;

    public Post save(Post post) {
        postList.add(post);
        return post;
    }

    public Long generateId() {
        return nextId++;
    }

    public List<Post> findAll() {
        return postList;
    }

    public Post findById(Long id) {
        for (Post post : postList) {
            if (id.equals(post.getId())) {
                return post;
            }
        }
        return null;
    }

    public void deleteById(Long id) {
        postList.removeIf(post -> id.equals(post.getId()));
    }
}