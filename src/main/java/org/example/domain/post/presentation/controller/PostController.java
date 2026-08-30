package org.example.domain.post.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.domain.post.presentation.dto.request.PostReactionRequest;
import org.example.domain.post.presentation.dto.response.PostReactionResponse;
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

    // POST /posts
    @Operation(summary = "게시글 좋아요", description = "게시글 좋아요를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 좋아요 성공"),
            @ApiResponse(responseCode = "400", description = "좋아요 유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "좋아요를 누를 수 없음 - 존재하지 않는 ID로 요청한 경우")
    })
    @PostMapping("/{postId}/like")
    public ResponseEntity<BaseResponse<PostReactionResponse>> saveLikePost(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable Long postId,
            @Valid @RequestBody PostReactionRequest request
    ) {
        PostReactionResponse response = postService.saveLikePost(postId, request);
        return BaseResponse.success(PostSuccessCode.POST_SAVE_LIKE, response);
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
    @GetMapping("/{postId}")
    public ResponseEntity<BaseResponse<PostResponse>> getPost(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable Long postId
    ) {
        return BaseResponse.success(PostSuccessCode.POST_READ, postService.getPost(postId));
    }

    // GET /posts/search
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<PostResponse>>> searchPosts(
            @RequestParam String keyword
    ) {
        return BaseResponse.success(PostSuccessCode.POST_LIST_READ, postService.searchPosts(keyword));
    }

    // PUT /posts/{id}
    @PutMapping("/{postId}")
    public ResponseEntity<BaseResponse<PostResponse>> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest request
    ) {
        PostResponse response = postService.updatePost(postId, request);
        return BaseResponse.success(PostSuccessCode.POST_UPDATED, response);
    }

    // DELETE /posts/{id}
    @DeleteMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> deletePost(
            @PathVariable Long postId
    ) {
        postService.deletePost(postId);
        return BaseResponse.success(PostSuccessCode.POST_DELETED, null);
    }

    // DELETE /posts/{postId}/like
    @Operation(summary = "게시글 좋아요 취소", description = "게시글 좋아요를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 취소 성공"),
            @ApiResponse(responseCode = "400", description = "좋아요 취소 유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "좋아요를 취소할 수 없음 - 존재하지 않는 ID로 요청한 경우")
    })
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<BaseResponse<PostReactionResponse>> deleteLikePost(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable Long postId,
            @Valid @RequestBody PostReactionRequest request
    ) {
        PostReactionResponse response = postService.deleteLikePost(postId, request);
        return BaseResponse.success(PostSuccessCode.POST_DELETE_LIKE, response);
    }
}
