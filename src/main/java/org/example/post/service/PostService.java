package org.example.post.service;


import org.example.post.domain.entity.Post;
import org.example.post.domain.exception.PostNotFoundException;
import org.example.post.domain.model.BoardType;
import org.example.post.presentation.dto.request.CreatePostRequest;
import org.example.post.presentation.dto.request.UpdatePostRequest;
import org.example.post.presentation.dto.response.CreatePostResponse;
import org.example.post.presentation.dto.response.PostResponse;
import org.example.post.domain.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // CREATE
    public CreatePostResponse createPost(CreatePostRequest request) {
        // 1. 유효성 검증
        request.validate();
        // 2. Post 도메인 객체 생성
        String createdAt = java.time.LocalDateTime.now().toString();
        Post post = new Post(
                postRepository.generateId(),
                request.title(),
                request.content(),
                request.author(),
                request.boardType(),
                createdAt
        );
        // 3. 저장
        Post create = postRepository.save(post);
        // 4. 응답 DTO 조립해서 반환
        return new CreatePostResponse(create.getId());
    }

    // READ - 목록 조회
    public List<PostResponse> getAllPosts(
            int page,
            int size,
            BoardType boardType
    ) {
        List<Post> posts;

        if (boardType == null) {
            posts = postRepository.findAll();
        } else {
            posts = postRepository.findAllByBoardType(boardType);
        }
        return posts.stream()
                .skip((long) page * size)
                .limit(size)
                .map(PostResponse::new)
                .toList();
    }

    // READ - 단건 (id를 알기 때문에 자동으로 BoardType을 알게 됨)
    public PostResponse getPost(Long id) {
        Post post = findPostOrThrow(id);
        return new PostResponse(post);
    }

    // UPDATE
    public void updatePost(Long id, UpdatePostRequest request) {
        request.validate();
        Post post = findPostOrThrow(id);
        Post updated = post.update(request.newTitle(), request.newContent());
        postRepository.save(updated);
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
