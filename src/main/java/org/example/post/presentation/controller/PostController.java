package org.example.post.presentation.controller;

import org.example.global.response.ApiResponse;
import org.example.post.domain.code.PostSuccessCode;
import org.example.post.presentation.dto.request.CreatePostRequest;
import org.example.post.presentation.dto.request.UpdatePostRequest;
import org.example.post.presentation.dto.response.CreatePostResponse;
import org.example.post.presentation.dto.response.PostResponse;
import org.example.post.service.PostService;
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
        return ApiResponse.success(PostSuccessCode.POST_CREATED, response);
    }

    // GET /posts
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts() {
        return ApiResponse.success(PostSuccessCode.POST_LIST_READ, postService.getAllPosts());
    }

    // GET /posts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @PathVariable Long id
    ) {
        return ApiResponse.success(PostSuccessCode.POST_READ, postService.getPost(id));
    }

    // PUT /posts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request
    ) {
        postService.updatePost(id, request);
        return ApiResponse.success(PostSuccessCode.POST_UPDATED, null);
    }

    // DELETE /posts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id
    ) {
        postService.deletePost(id);
        return ApiResponse.success(PostSuccessCode.POST_DELETED, null);
    }
}
