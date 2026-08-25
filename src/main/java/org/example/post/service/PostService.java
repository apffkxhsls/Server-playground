package org.example.post.service;


import org.example.post.domain.entity.Post;
import org.example.post.domain.entity.User;
import org.example.post.domain.exception.PostNotFoundException;
import org.example.post.domain.model.BoardType;
import org.example.post.domain.repository.PostRepository;
import org.example.post.domain.repository.UserRepository;
import org.example.post.presentation.dto.request.CreatePostRequest;
import org.example.post.presentation.dto.request.UpdatePostRequest;
import org.example.post.presentation.dto.response.CreatePostResponse;
import org.example.post.presentation.dto.response.PostResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // CREATE
    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request) {
        // 1. 유효성 검증
        request.validate();
        // 2. Post 도메인 객체 생성
        String createdAt = java.time.LocalDateTime.now().toString();
        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없습니다."));
        Post post = new Post(
                request.title(),
                request.content(),
                author,
                request.boardType(),
                createdAt
        );
        // 3. 저장
        Post create = postRepository.save(post);
        // 4. 응답 DTO 조립해서 반환
        return new CreatePostResponse(create.getId());
    }

    // READ - 목록 조회
    @Transactional(readOnly = true)
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
                .map(PostResponse::from)
                .toList();
    }

    // READ - 단건 (id를 알기 때문에 자동으로 BoardType을 알게 됨)
    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = findPostOrThrow(id);
        return PostResponse.from(post);
    }

    // UPDATE
    @Transactional  // 이 범위 안에서 조회한 Post를 JPA가 계속 관리
    public PostResponse updatePost(Long id, UpdatePostRequest request) {
        request.validate();
        Post post = findPostOrThrow(id);
        post.update(request.newTitle(), request.newContent());
        return PostResponse.from(post);
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
