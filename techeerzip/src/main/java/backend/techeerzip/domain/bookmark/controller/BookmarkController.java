package backend.techeerzip.domain.bookmark.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import backend.techeerzip.domain.bookmark.dto.request.BookmarkSaveRequest;
import backend.techeerzip.domain.bookmark.dto.response.BookmarkListResponse;
import backend.techeerzip.domain.bookmark.entity.BookmarkCategory;
import backend.techeerzip.domain.bookmark.service.BookmarkService;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "bookmarks", description = "북마크 API")
@RestController
@RequestMapping("/api/v3/bookmark")
@RequiredArgsConstructor
public class BookmarkController implements BookmarkSwagger {
    private final BookmarkService bookmarkService;
    private final CustomLogger logger;
    private static final String CONTEXT = "BookmarkController";

    @PostMapping
    @Override
    public ResponseEntity<Void> postBookmark(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestBody BookmarkSaveRequest request) {
        logger.info(
                "북마크 생성 요청 처리 중 - userId: {}, request: {} | context: {}", userId, request, CONTEXT);
        bookmarkService.createBookmark(userId, request);
        logger.info("북마크 생성 요청 처리 완료 | context: {}", CONTEXT);
        return ResponseEntity.status(201).build();
    }

    @GetMapping
    @Override
    public ResponseEntity<BookmarkListResponse> getBookmarkList(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestParam(required = false) BookmarkCategory category,
            @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        logger.info(
                "북마크 목록 조회 요청 처리 중 - userId: {}, category: {}, cursorId: {}, limit: {} | context: {}",
                userId,
                category,
                cursorId,
                limit,
                CONTEXT);
        BookmarkListResponse response =
                bookmarkService.getBookmarkList(userId, category, cursorId, limit);
        logger.info("북마크 목록 조회 요청 처리 완료 | context: {}", CONTEXT);
        return ResponseEntity.ok(response);
    }
}
