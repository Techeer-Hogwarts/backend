package backend.techeerzip.domain.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import backend.techeerzip.domain.like.dto.request.LikeSaveRequest;
import backend.techeerzip.domain.like.dto.response.LikeListResponse;
import backend.techeerzip.domain.like.entity.LikeCategory;
import lombok.RequiredArgsConstructor;
import backend.techeerzip.global.resolver.UserId;
import backend.techeerzip.domain.like.service.LikeService;
import backend.techeerzip.global.logger.CustomLogger;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "likes", description = "좋아요 API")
@RestController
@RequestMapping("/api/v3/likes")
@RequiredArgsConstructor
public class LikeController implements LikeSwagger {
    private final LikeService likeService;
    private final CustomLogger logger;
    private static final String CONTEXT = "LikeController";

    @PostMapping
    @Override
    public ResponseEntity<Void> postLike(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestBody LikeSaveRequest request) {
        logger.info("좋아요 생성 요청 처리 중 - userId: {}, request: {} | context: {}", userId, request, CONTEXT);
        likeService.createLike(userId, request);
        logger.info("좋아요 생성 요청 처리 완료 | context: {}", CONTEXT);
        return ResponseEntity.status(201).build();
    }

    @GetMapping
    @Override
    public ResponseEntity<LikeListResponse> getLikeList(
            @Parameter(hidden = true) @UserId Long userId,
            @RequestParam(required = false) LikeCategory category,
            @RequestParam(required = false, defaultValue = "0") Long cursorId,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "latest") String sortBy) {
        logger.info(
            "좋아요 목록 조회 요청 처리 중 - userId: {}, category: {}, cursorId: {}, limit: {}, sortBy: {} | context: {}",
            userId, category, cursorId, limit, sortBy, CONTEXT);
        LikeListResponse response = likeService.getLikeList(userId, category, cursorId, limit, sortBy);
        logger.info("좋아요 목록 조회 요청 처리 완료 | context: {}", CONTEXT);
        return ResponseEntity.ok(response);
    }
}
