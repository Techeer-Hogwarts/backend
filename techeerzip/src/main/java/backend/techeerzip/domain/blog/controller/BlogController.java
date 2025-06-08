package backend.techeerzip.domain.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.blog.dto.request.BlogBestQueryRequest;
import backend.techeerzip.domain.blog.dto.request.BlogListQueryRequest;
import backend.techeerzip.domain.blog.dto.response.BlogListResponse;
import backend.techeerzip.domain.blog.dto.response.BlogResponse;
import backend.techeerzip.domain.blog.service.BlogService;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "blogs", description = "블로그 API")
@RestController
@RequestMapping("/api/v3/blogs")
@RequiredArgsConstructor
public class BlogController implements BlogSwagger {
    private final BlogService blogService;
    private final CustomLogger logger;
    private static final String CONTEXT = "BlogController";

    @PostMapping
    @Override
    public ResponseEntity<Void> createSharedBlog(
            @Parameter(hidden = true) @UserId Long userId, @RequestParam String url) {
        logger.info("외부 블로그 게시 요청 처리 중 - userId: {}, url: {} | context: {}", userId, url, CONTEXT);
        blogService.createSharedBlog(userId, url);
        logger.info("외부 블로그 게시 요청 처리 완료 | context: {}", CONTEXT);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{blogId}")
    @Override
    public ResponseEntity<Void> increaseBlogViewCount(@PathVariable Long blogId) {
        logger.info("블로그 조회수 증가 처리 중 - blogId: {} ", blogId, CONTEXT);
        blogService.increaseBlogViewCount(blogId);
        logger.info("블로그 조회수 증가 처리 완료 - blogId: {} ", blogId, CONTEXT);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/best")
    @Override
    public ResponseEntity<BlogListResponse> getBestBlogs(
            @ModelAttribute BlogBestQueryRequest query) {
        logger.info("인기글 목록 조회 처리 중 - cursorId: {}, limit: {} ", query);
        BlogListResponse result = blogService.getBestBlogs(query);
        logger.info("인기글 목록 조회 처리 완료 - cursorId: {}, limit: {} ", query, CONTEXT);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @Override
    public ResponseEntity<BlogListResponse> getBlogList(
            @ModelAttribute BlogListQueryRequest query) {
        logger.info("블로그 목록 조회 및 검색 처리 중 - query: {} ", query, CONTEXT);
        BlogListResponse result = blogService.getBlogList(query);
        logger.info("블로그 목록 조회 및 검색 처리 완료 - query: {} ", query, CONTEXT);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{blogId}")
    @Override
    public ResponseEntity<BlogResponse> getBlog(@PathVariable Long blogId) {
        logger.info("단일 블로그 게시물 조회 처리 중 - blogId: {} ", blogId, CONTEXT);
        BlogResponse result = blogService.getBlog(blogId);
        logger.info("단일 블로그 게시물 조회 처리 완료 - blogId: {} ", blogId, CONTEXT);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{blogId}")
    @Override
    public ResponseEntity<BlogResponse> deleteBlog(
            @Parameter(hidden = true) @UserId Long userId, @PathVariable Long blogId) {
        logger.info("블로그 게시물 삭제 처리 중 - blogId: {}, userId: {} ", blogId, userId, CONTEXT);
        blogService.deleteBlog(blogId, userId);
        logger.info("블로그 게시물 삭제 처리 완료 - blogId: {}, userId: {} ", blogId, userId, CONTEXT);
        return ResponseEntity.noContent().build();
    }
}
