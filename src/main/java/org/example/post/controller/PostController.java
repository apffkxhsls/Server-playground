package org.example.post.controller;

import org.example.global.response.ApiResponse;
import org.example.post.controller.dto.request.CreatePostRequest;
import org.example.post.controller.dto.request.UpdatePostRequest;
import org.example.post.controller.dto.response.PostResponse;
import org.example.post.exception.PostNotFoundException;
import org.example.post.service.PostService;

import java.util.List;

public class PostController {
    private final PostService postService = new PostService();

    // POST /posts
    public ApiResponse<PostResponse> createPost(CreatePostRequest request) {
        try {
            PostResponse response = postService.createPost(request);
            return ApiResponse.success("게시글 등록 완료!", response);
        } catch (PostNotFoundException e) {
            return ApiResponse.failure(e.getMessage());
        }
    }

    // GET /posts 📝 과제
    public ApiResponse<List<PostResponse>> getAllPosts() {
        try {
            return ApiResponse.success("게시글 목록 조회 성공", postService.getAllPosts());
        } catch (PostNotFoundException e) {
            return ApiResponse.failure(e.getMessage());
        }
    }

    // GET /posts/{id} 📝 과제
    public ApiResponse<PostResponse> getPost(Long id) {
        try {
            return ApiResponse.success("게시글 조회 성공", postService.getPost(id));
        } catch (PostNotFoundException e) {
            return ApiResponse.failure(e.getMessage());
        }
    }

    // PUT /posts/{id} 📝 과제
    public ApiResponse<Void> updatePost(Long id, UpdatePostRequest request) {
        try {
            postService.updatePost(id, request);
            return ApiResponse.success("게시글 수정 완료", null);
        } catch (PostNotFoundException e) {
            return ApiResponse.failure(e.getMessage());
        }
    }

    // DELETE /posts/{id} 📝 과제
    public ApiResponse<Void> deletePost(Long id) {
        try {
            postService.deletePost(id);
            return ApiResponse.success("게시글 삭제 완료", null);
        } catch (PostNotFoundException e) {
            return ApiResponse.failure(e.getMessage());
        }
    }
}