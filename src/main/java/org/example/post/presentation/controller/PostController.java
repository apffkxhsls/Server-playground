package org.example.post.presentation.controller;

import org.example.global.response.ApiResponse;
import org.example.post.presentation.dto.request.CreatePostRequest;
import org.example.post.presentation.dto.request.UpdatePostRequest;
import org.example.post.presentation.dto.response.CreatePostResponse;
import org.example.post.presentation.dto.response.PostResponse;
import org.example.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // POST /posts
    @PostMapping
    public ResponseEntity<ApiResponse<CreatePostResponse>> createPost(
            @RequestBody CreatePostRequest request
    ) {
        CreatePostResponse response = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("게시글 등록 완료!", response));
    }

    // GET /posts
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts() {
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회 성공", postService.getAllPosts()));
    }

    // GET /posts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("게시글 조회 성공", postService.getPost(id)));
    }

    // PUT /posts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request
    ) {
        postService.updatePost(id, request);
        return ResponseEntity.ok(ApiResponse.success("게시글 수정 완료", null));
    }

    // DELETE /posts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id
    ) {
        postService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("게시글 삭제 완료", null));
    }
}
