package backend.techeerzip.domain.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import backend.techeerzip.domain.blog.dto.request.BlogBestQueryRequest;
import backend.techeerzip.domain.blog.dto.request.BlogListQueryRequest;
import backend.techeerzip.domain.blog.dto.response.BlogListResponse;
import backend.techeerzip.domain.blog.dto.response.BlogResponse;
import backend.techeerzip.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 이 인터페이스는 Swagger 문서 생성을 위한 어노테이션 분리용입니다. 실제 컨트롤러에서는 구현하지 않으며, 메서드 호출 목적도 없습니다. 따라서 IntelliJ 등에서
 * 사용되지 않는 메서드로 표시될 수 있습니다.
 */
@Tag(name = "blogs", description = "블로그 API")
public interface BlogSwagger {

    @Operation(summary = "외부 블로그 게시", description = "외부 블로그를 게시합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게시 성공"),
        @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> createSharedBlog(
            @Parameter(hidden = true) Long userId,
            @Parameter(description = "블로그 URL") @RequestParam String url) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "블로그 조회수 증가", description = "블로그 조회수 증가시킵니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회수 증가 성공"),
        @ApiResponse(
                responseCode = "404",
                description = "블로그를 찾을 수 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<Void> increaseBlogViewCount(
            @Parameter(description = "블로그 ID") @PathVariable Long blogId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(
            summary = "블로그 게시물의 인기글 목록 조회",
            description = "2주간의 글 중 (조회수 + 좋아요수*10)을 기준으로 인기글을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BlogListResponse.class)))
    default ResponseEntity<BlogListResponse> getBestBlogs(
            @Parameter(description = "검색 조건") BlogBestQueryRequest query) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(
            summary = "블로그 게시물 목록 조회 및 검색",
            description =
                    "블로그 게시물을 조회하고 검색합니다. sortBy에는 latest, viewCount, name (최신순, 조회순, 가나다순의 옵션이 있습니다), 기본값은 latest입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BlogListResponse.class)))
    default ResponseEntity<BlogListResponse> getBlogList(
            @Parameter(description = "검색 조건") BlogListQueryRequest query) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "블로그 단일 조회", description = "블로그 ID를 기반으로 단일 블로그 게시물을 조회합니다.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = BlogResponse.class))),
        @ApiResponse(
                responseCode = "404",
                description = "블로그를 찾을 수 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<BlogResponse> getBlog(
            @Parameter(description = "블로그 ID") @PathVariable Long blogId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }

    @Operation(summary = "블로그 삭제", description = "블로그 ID를 기반으로 단일 블로그 게시물을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(
                responseCode = "404",
                description = "블로그를 찾을 수 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
                responseCode = "403",
                description = "권한 없음",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    default ResponseEntity<BlogResponse> deleteBlog(
            @Parameter(hidden = true) Long userId,
            @Parameter(description = "블로그 ID") @PathVariable Long blogId) {
        throw new UnsupportedOperationException("Swagger 문서 전용 인터페이스입니다.");
    }
}
