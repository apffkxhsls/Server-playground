package org.example.domain.post.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.domain.post.domain.code.PostSuccessCode;
import org.example.domain.post.domain.model.BoardType;
import org.example.domain.post.presentation.dto.request.CreatePostRequest;
import org.example.domain.post.presentation.dto.request.UpdatePostRequest;
import org.example.domain.post.presentation.dto.response.CreatePostResponse;
import org.example.domain.post.presentation.dto.response.PostReactionResponse;
import org.example.domain.post.presentation.dto.response.PostResponse;
import org.example.domain.post.service.PostService;
import org.example.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    /**
     * 새로운 게시글을 작성하고 생성된 게시글 ID를 반환한다.
     *
     * @param request        게시글 제목, 본문, 게시판 종류를 담은 요청
     * @param authentication 현재 인증된 사용자 정보
     * @return 생성된 게시글 ID를 담은 공통 응답
     */
    // POST /posts
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패 (제목 누락 또는 글자 수 초과)")
    })
    @PostMapping
    public ResponseEntity<BaseResponse<CreatePostResponse>> createPost(
            @Valid @RequestBody CreatePostRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());

        CreatePostResponse response = postService.createPost(request, userId);
        return BaseResponse.success(PostSuccessCode.POST_CREATED, response);
    }


    /**
     * 페이지 번호와 게시판 종류 조건에 따라 게시글 목록을 조회한다.
     *
     * @param page      0부터 시작하는 페이지 번호
     * @param size      한 번에 조회할 게시글 수
     * @param boardType 조회할 게시판 종류, 없으면 전체 게시글을 조회한다
     * @return 게시글 목록을 담은 공통 응답
     */
    // GET /posts
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
    })
    @GetMapping
    public ResponseEntity<BaseResponse<List<PostResponse>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // boardType 파라미터는 없어도 요청을 오류로 처리하지 않는다는 의미
            @RequestParam(required = false) BoardType boardType
    ) {
        return BaseResponse.success(PostSuccessCode.POST_LIST_READ, postService.getAllPosts(page, size, boardType));
    }


    /**
     * 게시글 ID로 게시글의 상세 정보와 현재 공감 수를 조회한다.
     *
     * @param postId 조회할 게시글 ID
     * @return 게시글 상세 정보를 담은 공통 응답
     */
    // GET /posts/{id}
    @Operation(
            summary = "게시글 단건 조회",           // Swagger UI에서 API 이름으로 보임
            description = "게시글 ID로 특정 게시글을 조회합니다. 삭제된 게시글은 조회되지 않아요."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 — ID가 숫자가 아닌 경우"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음 — 존재하지 않는 ID로 요청한 경우")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<BaseResponse<PostResponse>> getPost(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable Long postId
    ) {
        return BaseResponse.success(PostSuccessCode.POST_READ, postService.getPost(postId));
    }

    /**
     * 사용자의 게시글 공감을 등록하고, 변경된 전체 공감 수를 반환한다.
     *
     * @param postId         공감할 게시글 ID
     * @param authentication 현재 인증된 사용자 정보
     * @return 게시글 ID와 변경된 공감 수
     * @throws IllegalArgumentException 이미 공감했거나 사용자가 존재하지 않는 경우
     */
    // POST /posts/{postId}/like
    @Operation(summary = "게시글 공감", description = "게시글 공감을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 공감 성공"),
            @ApiResponse(responseCode = "400", description = "공감 유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "공감를 누를 수 없음 - 존재하지 않는 ID로 요청한 경우")
    })
    @PostMapping("/{postId}/like")
    public ResponseEntity<BaseResponse<PostReactionResponse>> saveLikePost(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());

        PostReactionResponse response = postService.saveLikePost(postId, userId);
        return BaseResponse.success(PostSuccessCode.POST_SAVE_LIKE, response);
    }


    /**
     * 사용자가 등록한 게시글 공감을 취소하고 변경된 전체 공감 수를 반환한다.
     *
     * @param postId         공감을 취소할 게시글 ID
     * @param authentication 현재 인증된 사용자 정보
     * @return 게시글 ID와 변경된 공감 수를 담은 공통 응답
     * @throws IllegalArgumentException 공감 기록이 없거나 사용자 또는 게시글이 존재하지 않는 경우
     */
    // DELETE /posts/{postId}/like
    @Operation(summary = "게시글 공감 취소", description = "게시글 공감를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 공감 취소 성공"),
            @ApiResponse(responseCode = "400", description = "공감 취소 유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "공감를 취소할 수 없음 - 존재하지 않는 ID로 요청한 경우")
    })
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<BaseResponse<PostReactionResponse>> deleteLikePost(
            @Parameter(description = "게시글 ID", example = "1", required = true)
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());

        PostReactionResponse response = postService.deleteLikePost(postId, userId);
        return BaseResponse.success(PostSuccessCode.POST_DELETE_LIKE, response);
    }


    /**
     * 제목 키워드와 작성자 닉네임 조건으로 게시글을 검색한다.
     *
     * @param keyword  제목 검색어, 없으면 제목 조건을 적용하지 않는다
     * @param nickname 작성자 닉네임 검색어, 없으면 닉네임 조건을 적용하지 않는다
     * @return 검색 조건에 일치하는 게시글 목록을 담은 공통 응답
     */
    // GET /posts/search
    @Operation(summary = "게시글 검색", description = "게시글을 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<PostResponse>>> searchPosts(
            // (required = false)로 동적 구현
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String nickname
    ) {
        return BaseResponse.success(PostSuccessCode.POST_LIST_READ, postService.searchPosts(keyword, nickname));
    }


    /**
     * 게시글 ID에 해당하는 게시글의 제목과 본문을 수정한다.
     *
     * @param postId  수정할 게시글 ID
     * @param request 수정할 제목과 본문을 담은 요청
     * @return 수정된 게시글 정보를 담은 공통 응답
     * @throws IllegalArgumentException 수정 제목이 비어 있는 경우
     */
    // PUT /posts/{id}
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "게시글 수정 요청값 유효성 검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 필요 - Access Token 누락 또는 유효하지 않은 토큰"),
            @ApiResponse(responseCode = "403", description = "게시글 작성자가 아님"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음 - 존재하지 않는 게시글")
    })
    @PutMapping("/{postId}")
    public ResponseEntity<BaseResponse<PostResponse>> updatePost(
            @PathVariable Long postId,
            Authentication authentication,
            @Valid @RequestBody UpdatePostRequest request
    ) {
        Long userId = Long.parseLong(authentication.getName());

        PostResponse response = postService.updatePost(postId, userId, request);
        return BaseResponse.success(PostSuccessCode.POST_UPDATED, response);
    }


    /**
     * 게시글 ID에 해당하는 게시글을 소프트 삭제한다.
     *
     * @param postId 삭제할 게시글 ID
     * @return 삭제 결과를 담은 공통 응답
     */
    // DELETE /posts/{id}
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 - Access Token 누락 또는 유효하지 않은 토큰"),
            @ApiResponse(responseCode = "403", description = "게시글 작성자가 아님"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음 - 존재하지 않는 게시글")
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());

        postService.deletePost(postId, userId);
        return BaseResponse.success(PostSuccessCode.POST_DELETED, null);
    }
}
