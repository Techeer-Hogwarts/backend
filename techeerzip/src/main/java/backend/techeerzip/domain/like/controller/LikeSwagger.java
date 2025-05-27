package backend.techeerzip.domain.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import backend.techeerzip.domain.like.dto.request.LikeSaveRequest;
import backend.techeerzip.domain.like.dto.response.LikeListResponse;
import backend.techeerzip.domain.like.entity.LikeCategory;
import backend.techeerzip.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "likes", description = "좋아요 API")
public interface LikeSwagger {

    @Operation(
            summary = "좋아요 생성 및 설정 변경",
            description =
                    "좋아요를 저장 혹은 설정을 변경합니다.\n\n카테고리는 SESSION, BLOG, RESUME 입니다.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "좋아요 생성/변경 성공"),
                @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                @ApiResponse(responseCode = "401", description = "인증 실패"),
                @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음")
            })
    default ResponseEntity<Void> postLike(
            @Parameter(hidden = true) @UserId Long userId,
            @Parameter(description = "좋아요 요청 정보") LikeSaveRequest request) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(
            summary = "유저 별 좋아요 목록 조회",
            description =
                    "유저 별 좋아요한 콘텐츠 목록을 조회합니다.\n\n" +
                    "카테고리는 SESSION, BLOG, RESUME 입니다.\n" +
                    "최신순으로 정렬됩니다.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "좋아요 목록 조회 성공"),
                @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                @ApiResponse(responseCode = "401", description = "인증 실패"),
                @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음")
            })
    default ResponseEntity<LikeListResponse> getLikeList(
            @Parameter(hidden = true) @UserId Long userId,
            @Parameter(description = "카테고리 (SESSION, BLOG, RESUME)") @RequestParam(required = false) LikeCategory category,
            @Parameter(description = "마지막으로 조회한 좋아요의 ID") @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @Parameter(description = "가져올 개수") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }
}
