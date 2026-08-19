package org.example.post.service;


import org.example.post.controller.dto.request.CreatePostRequest;
import org.example.post.controller.dto.request.UpdatePostRequest;
import org.example.post.controller.dto.response.PostResponse;
import org.example.post.domain.Post;
import org.example.post.exception.PostNotFoundException;
import org.example.post.repository.PostRepository;

import java.util.List;

public class PostService {
    private final PostRepository postRepository = new PostRepository();

    // CREATE
    public PostResponse createPost(CreatePostRequest request) {
        request.validate();
        String createdAt = java.time.LocalDateTime.now().toString();
        Post post = new Post(postRepository.generateId(), request.title(), request.content(), request.author(), createdAt);
        postRepository.save(post);
        return new PostResponse(post);
    }

    // READ - 전체 📝 과제
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostResponse::new)
                .toList();
    }

    // READ - 단건 📝 과제
    public PostResponse getPost(Long id) {
        Post post = findPostOrThrow(id);
        return new PostResponse(post);
    }

    // UPDATE 📝 과제
    public void updatePost(Long id, UpdatePostRequest request) {
        request.validate();
        Post post = findPostOrThrow(id);
        post.update(request.newTitle(), request.newContent());
    }

    // DELETE 📝 과제
    public void deletePost(Long id) {
        findPostOrThrow(id);
        postRepository.deleteById(id);
    }

    private Post findPostOrThrow(Long id) {
        Post post = postRepository.findById(id);
        if (post == null) {
            throw new PostNotFoundException();
        }
        return post;
    }
}