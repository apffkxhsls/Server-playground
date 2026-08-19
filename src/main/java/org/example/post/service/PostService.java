package org.example.post.service;


import org.example.post.controller.dto.request.CreatePostRequest;
import org.example.post.controller.dto.response.CreatePostResponse;
import org.example.post.domain.Post;
import org.example.post.repository.PostRepository;

import java.util.List;

public class PostService {
    private final PostRepository postRepository = new PostRepository();

    // CREATE
    public CreatePostResponse createPost(CreatePostRequest request) {
        request.validate();
        String createdAt = java.time.LocalDateTime.now().toString();
        Post post = new Post(postRepository.generateId(), request.title(), request.content(), request.author(), createdAt);
        postRepository.save(post);
        return new CreatePostResponse(post.getId(), "게시글 등록 완료!");
    }

    // READ - 전체 📝 과제
    public List<CreatePostResponse> getAllPosts() {
        // TODO
        return null;
    }

    // READ - 단건 📝 과제
    public CreatePostResponse getPost(Long id) {
        // TODO
        return null;
    }

    // UPDATE 📝 과제
    public void updatePost(Long id, String newTitle, String newContent) {
        // TODO
    }

    // DELETE 📝 과제
    public void deletePost(Long id) {
        // TODO
    }
}