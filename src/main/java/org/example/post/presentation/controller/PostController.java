package org.example.post.presentation.controller;

import jakarta.validation.Valid;
import org.example.global.response.BaseResponse;
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
    public ResponseEntity<BaseResponse<CreatePostResponse>> createPost(
            @Valid @RequestBody CreatePostRequest request
    ) {
        CreatePostResponse response = postService.createPost(request);
        return BaseResponse.success(PostSuccessCode.POST_CREATED, response);
    }

    // GET /posts
    @GetMapping
    public ResponseEntity<BaseResponse<List<PostResponse>>> getAllPosts() {
        return BaseResponse.success(PostSuccessCode.POST_LIST_READ, postService.getAllPosts());
    }

    // GET /posts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PostResponse>> getPost(
            @PathVariable Long id
    ) {
        return BaseResponse.success(PostSuccessCode.POST_READ, postService.getPost(id));
    }

    // PUT /posts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> updatePost(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request
    ) {
        postService.updatePost(id, request);
        return BaseResponse.success(PostSuccessCode.POST_UPDATED, null);
    }

    // DELETE /posts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deletePost(
            @PathVariable Long id
    ) {
        postService.deletePost(id);
        return BaseResponse.success(PostSuccessCode.POST_DELETED, null);
    }
}
