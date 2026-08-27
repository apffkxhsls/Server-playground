package org.example.domain.post.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.global.response.BaseResponse;
import org.example.domain.post.domain.code.PostSuccessCode;
import org.example.domain.post.domain.model.BoardType;
import org.example.domain.post.presentation.dto.request.CreatePostRequest;
import org.example.domain.post.presentation.dto.request.UpdatePostRequest;
import org.example.domain.post.presentation.dto.response.CreatePostResponse;
import org.example.domain.post.presentation.dto.response.PostResponse;
import org.example.domain.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Post", description = "게시글 관련 API")
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // POST /posts
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패 (제목/내용 누락 또는 글자 수 초과)")
    })
    @PostMapping
    public ResponseEntity<BaseResponse<CreatePostResponse>> createPost(
            @Valid @RequestBody CreatePostRequest request
    ) {
        CreatePostResponse response = postService.createPost(request);
        return BaseResponse.success(PostSuccessCode.POST_CREATED, response);
    }

    // GET /posts
    @GetMapping
    public ResponseEntity<BaseResponse<List<PostResponse>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // boardType 파라미터는 없어도 요청을 오류로 처리하지 않는다는 의미
            @RequestParam(required = false) BoardType boardType
    ) {
        return BaseResponse.success(PostSuccessCode.POST_LIST_READ, postService.getAllPosts(page, size, boardType));
    }

    // GET /posts/{id}
    @Operation(
            summary = "게시글 단건 조회",           // Swagger UI에서 API 이름으로 보임
            description = "게시글 ID로 특정 게시글을 조회합니다. 삭제된 게시글은 조회되지 않아요."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음 — 존재하지 않는 ID로 요청한 경우"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 — ID가 숫자가 아닌 경우")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PostResponse>> getPost(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        return BaseResponse.success(PostSuccessCode.POST_READ, postService.getPost(id));
    }

    // PUT /posts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<PostResponse>> updatePost(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request
    ) {
        PostResponse response = postService.updatePost(id, request);
        return BaseResponse.success(PostSuccessCode.POST_UPDATED, response);
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
