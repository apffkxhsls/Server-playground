package org.example.post.controller;

import org.example.global.response.ApiResponse;
import org.example.post.controller.dto.request.CreatePostRequest;
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
            return null;
        }
    }

    // GET /posts/{id} 📝 과제
    public PostResponse getPost(Long id) {
        // TODO: postService.getPost(id) 호출, 예외 발생 시 null 반환
        return null;
    }

    // PUT /posts/{id} 📝 과제
    public void updatePost(Long id, String newTitle, String newContent) {
        // TODO: postService.updatePost() 호출, 예외 발생 시 에러 메시지 출력
    }

    // DELETE /posts/{id} 📝 과제
    public void deletePost(Long id) {
        // TODO: postService.deletePost() 호출, 예외 발생 시 에러 메시지 출력
    }
}