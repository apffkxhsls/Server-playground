package org.example.post.service;


import org.example.post.presentation.dto.request.CreatePostRequest;
import org.example.post.presentation.dto.request.UpdatePostRequest;
import org.example.post.presentation.dto.response.PostResponse;
import org.example.post.domain.Post;
import org.example.post.domain.exception.PostNotFoundException;
import org.example.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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

    // READ - 전체
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostResponse::new)
                .toList();
    }

    // READ - 단건
    public PostResponse getPost(Long id) {
        Post post = findPostOrThrow(id);
        return new PostResponse(post);
    }

    // UPDATE
    public void updatePost(Long id, UpdatePostRequest request) {
        request.validate();
        Post post = findPostOrThrow(id);
        post.update(request.newTitle(), request.newContent());
    }

    // DELETE
    public void deletePost(Long id) {
        findPostOrThrow(id);
        postRepository.deleteById(id);
    }

    private Post findPostOrThrow(Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.orElseThrow(PostNotFoundException::new);
    }
}