package backend.techeerzip.domain.blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.blog.dto.request.BlogsQueryRequest;
import backend.techeerzip.domain.blog.dto.response.BlogResponse;
import backend.techeerzip.domain.blog.service.BlogService;
import backend.techeerzip.global.logger.CustomLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "blogs", description = "블로그 API")
@RestController
@RequestMapping("/api/v3/blogs")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    private final CustomLogger logger;
    private static final String CONTEXT = "BlogController";

    @Operation(summary = "외부 블로그 게시", description = "외부 블로그를 게시합니다.")
    @PostMapping
    public ResponseEntity<Void> createSharedBlog(
            @RequestParam Long userId, @RequestParam String url) {
        logger.info("외부 블로그 게시 요청 처리 중 - userId: {}, url: {} | context: {}", userId, url, CONTEXT);
        blogService.createSharedBlog(userId, url);
        logger.info("외부 블로그 게시 요청 처리 완료 | context: {}", CONTEXT);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "블로그 조회수 증가", description = "블로그 조회수 증가시킵니다.")
    @PutMapping("/{blogId}")
    public ResponseEntity<Void> increaseBlogViewCount(@PathVariable Long blogId) {
        logger.info("블로그 조회수 증가 처리 중" + CONTEXT);
        blogService.increaseBlogViewCount(blogId);
        logger.info("블로그 조회수 증가 처리 완료" + CONTEXT);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "블로그 게시물의 인기글 목록 조회", description = "2주간의 글 중 (조회수 + 좋아요수*10)을 기준으로 인기글을 조회합니다.")
    @GetMapping("/best")
    public ResponseEntity<List<BlogResponse>> getBestBlogs(
            @Parameter(description = "페이지네이션 정보") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        logger.info("인기글 목록 조회 처리 중 - page: %d, size: %d" + page + size + CONTEXT);
        List<BlogResponse> result = blogService.getBestBlogs(page, size);
        logger.info("인기글 목록 조회 처리 완료" + CONTEXT);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "블로그 게시물 목록 조회 및 검색", description = "블로그 게시물을 조회하고 검색합니다.")
    @GetMapping
    public ResponseEntity<List<BlogResponse>> getBlogList(
            @Parameter(description = "검색 조건") BlogsQueryRequest query) {
        logger.info("블로그 목록 조회 및 검색 처리 중 - query: %s" + query + CONTEXT);
        List<BlogResponse> result = blogService.getBlogList(query);
        logger.info("블로그 목록 조회 및 검색 처리 완료" + CONTEXT);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "유저 별 블로그 게시물 목록 조회", description = "지정된 유저의 블로그 게시물을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BlogResponse>> getBlogsByUser(
            @PathVariable Long userId,
            @Parameter(description = "페이지네이션 정보") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        logger.info(
                "유저 별 블로그 게시물 목록 조회 처리 중 - userId: %d, page: %d, size: %d"
                        + userId
                        + page
                        + size
                        + CONTEXT);
        List<BlogResponse> result = blogService.getBlogsByUser(userId, page, size);
        logger.info("유저 별 블로그 게시물 목록 조회 처리 완료" + CONTEXT);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "블로그 단일 조회", description = "블로그 ID를 기반으로 단일 블로그 게시물을 조회합니다.")
    @GetMapping("/{blogId}")
    public ResponseEntity<BlogResponse> getBlog(@PathVariable Long blogId) {
        logger.info("단일 블로그 게시물 조회 처리 중 - blogId: %d" + blogId + CONTEXT);
        BlogResponse result = blogService.getBlog(blogId);
        logger.info("단일 블로그 게시물 조회 처리 완료" + CONTEXT);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "블로그 삭제", description = "블로그 ID를 기반으로 단일 블로그 게시물을 삭제합니다.")
    @DeleteMapping("/{blogId}")
    public ResponseEntity<BlogResponse> deleteBlog(@PathVariable Long blogId) {
        logger.info("블로그 게시물 삭제 처리 중 - blogId: %d" + blogId + CONTEXT);
        BlogResponse result = blogService.deleteBlog(blogId);
        logger.info("블로그 게시물 삭제 처리 완료" + CONTEXT);
        return ResponseEntity.ok(result);
    }
}
