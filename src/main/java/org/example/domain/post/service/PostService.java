package org.example.domain.post.service;


import org.example.domain.post.domain.code.PostErrorCode;
import org.example.domain.post.domain.entity.Post;
import org.example.domain.post.domain.entity.PostReaction;
import org.example.domain.post.domain.exception.PostNotFoundException;
import org.example.domain.post.domain.model.BoardType;
import org.example.domain.post.domain.repository.PostReactionRepository;
import org.example.domain.post.domain.repository.PostRepository;
import org.example.domain.post.presentation.dto.request.CreatePostRequest;
import org.example.domain.post.presentation.dto.request.UpdatePostRequest;
import org.example.domain.post.presentation.dto.response.CreatePostResponse;
import org.example.domain.post.presentation.dto.response.PostLikeCount;
import org.example.domain.post.presentation.dto.response.PostReactionResponse;
import org.example.domain.post.presentation.dto.response.PostResponse;
import org.example.domain.user.domain.entity.User;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.global.exception.BaseException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostReactionRepository postReactionRepository;

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository,
            PostReactionRepository postReactionRepository
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postReactionRepository = postReactionRepository;
    }

    // CREATE
    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request, Long userId) {
        // 2. Post 도메인 객체 생성
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없습니다."));
        Post post = new Post(
                request.title(),
                request.content(),
                user,
                request.boardType()
        );
        // 3. 저장
        Post create = postRepository.save(post);
        // 4. 응답 DTO 조립해서 반환
        return new CreatePostResponse(create.getId());
    }

    // POST_REACTION - Like
    @Transactional
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public PostReactionResponse saveLikePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post = postRepository
                .findByIdWithOptimisticLock(postId)
                .orElseThrow(PostNotFoundException::new);
        Optional<PostReaction> postReaction = postReactionRepository.findByUserAndPost(user, post);

        if (postReaction.isPresent()) {
            throw new IllegalArgumentException("공감이 이미 눌러져있습니다.");
        } else {
            PostReaction likeReaction = new PostReaction(user, post);
            postReactionRepository.save(likeReaction);
            long likeCount = postReactionRepository.countByPost(post);

            return new PostReactionResponse(postId, likeCount);
        }
    }

    // POST_REACTION - Like 취소
    @Transactional
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public PostReactionResponse deleteLikePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post = postRepository
                .findByIdWithOptimisticLock(postId)
                .orElseThrow(PostNotFoundException::new);
        Optional<PostReaction> postReaction = postReactionRepository.findByUserAndPost(user, post);

        if (postReaction.isEmpty()) {
            throw new IllegalArgumentException("공감이 눌려있지 않습니다.");
        } else {
            postReactionRepository.delete(postReaction.get());
            long likeCount = postReactionRepository.countByPost(post);

            return new PostReactionResponse(postId, likeCount);
        }
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
            posts = postRepository.findAllWithUser();
        } else {
            posts = postRepository.findAllByBoardTypeWithUser(boardType);
        }

        List<Post> pagedPosts = posts.stream()
                .skip((long) page * size)
                .limit(size)
                .toList();

        List<PostLikeCount> likes = postReactionRepository.findLikeCountsByPostIn(pagedPosts);
        Map<Long, Long> likeCountMap = likes.stream()
                .collect(Collectors.toMap(PostLikeCount::postId, PostLikeCount::likeCount));

        return pagedPosts.stream()
                .map(post -> {
                    long likeCount = likeCountMap.getOrDefault(post.getId(), 0L);
                    return PostResponse.from(post, likeCount);
                })
                .toList();
    }

    // READ - 단건 (id를 알기 때문에 자동으로 BoardType을 알게 됨)
    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = findPostOrThrow(id);

        long likeCount = postReactionRepository.countByPost(post);

        return PostResponse.from(post, likeCount);
    }

    // UPDATE
    @Transactional  // 이 범위 안에서 조회한 Post를 JPA가 계속 관리
    public PostResponse updatePost(Long id, Long userId, UpdatePostRequest request) {
        Post post = findPostOrThrow(id);
        request.validate();
        long likeCount = postReactionRepository.countByPost(post);

        validatePostOwner(post, userId);
        post.update(request.newTitle(), request.newContent());
        return PostResponse.from(post, likeCount);
    }

    // DELETE
    @Transactional
    public void deletePost(Long id, Long userId) {
        Post post = findPostOrThrow(id);

        validatePostOwner(post, userId);

        postRepository.delete(post);
    }

    private Post findPostOrThrow(Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.orElseThrow(PostNotFoundException::new);
    }

    private void validatePostOwner(Post post, Long userId) {
        if (!post.getUser().getId().equals(userId)) {
            throw new BaseException(PostErrorCode.POST_FORBIDDEN);
        }
    }

    // SEARCH
    @Transactional(readOnly = true)
    public List<PostResponse> searchPosts(String keyword, String nickname) {
        List<Post> posts = postRepository.searchByTitleAndUser(keyword, nickname);

        List<PostLikeCount> likes = postReactionRepository.findLikeCountsByPostIn(posts);
        Map<Long, Long> likeCountMap = likes.stream()
                .collect(Collectors.toMap(PostLikeCount::postId, PostLikeCount::likeCount));

        return posts.stream()
                .map(post -> {
                    long likeCount = likeCountMap.getOrDefault(post.getId(), 0L);
                    return PostResponse.from(post, likeCount);
                })
                .toList();
    }
}
