package backend.techeerzip.domain.session.controller;

import backend.techeerzip.domain.session.dto.request.SessionCreateRequest;
import backend.techeerzip.domain.session.dto.response.SessionResponse;
import backend.techeerzip.domain.session.service.SessionService;
import backend.techeerzip.domain.session.dto.request.CursorPageViewCountRequest;
import backend.techeerzip.domain.session.dto.response.CursorPageViewCountResponse;
import backend.techeerzip.domain.session.dto.request.CursorPageCreatedAtRequest;
import backend.techeerzip.domain.session.dto.response.CursorPageCreatedAtResponse;
import backend.techeerzip.global.resolver.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "세션", description = "세션 관련 API")
@RestController
@RequestMapping("/api/v3/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @Operation(summary = "세션 게시물 게시", description = "세션 게시물을 게시합니다.")
    @PostMapping
    public ResponseEntity<Long> createSession(
            @RequestBody @Valid SessionCreateRequest request,
            @Parameter(hidden = true) @UserId Long userId
    ) {
        return ResponseEntity.ok(sessionService.createSession(request, userId));
    }

    @Operation(summary = "세션 게시물 수정", description = "세션 게시물을 수정합니다.")
    @PutMapping("/{sessionId}")
    public ResponseEntity<Void> updateSession(
            @RequestBody @Valid SessionCreateRequest request,
            @PathVariable Long sessionId,
            @Parameter(hidden = true) @UserId Long userId
    ) {
        sessionService.updateSession(request, sessionId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "세션 게시물 목록 조회", description = "세션 게시물 목록을 커서 기반 페이지네이션으로 조회합니다.")
    @GetMapping
    public ResponseEntity<CursorPageCreatedAtResponse<SessionResponse>> getAllSessions(
            @ParameterObject @Valid CursorPageCreatedAtRequest request) {
        return ResponseEntity.ok(sessionService.getAllSessions(request));
    }

    @Operation(summary = "유저 별 세션 게시물 목록 조회", description = "유저 별 세션 게시물 목록을 커서 기반 페이지네이션으로 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<CursorPageCreatedAtResponse<SessionResponse>> getAllSessionsByUserId(
            @PathVariable Long userId,
            @ParameterObject @Valid CursorPageCreatedAtRequest request
    ) {
        return ResponseEntity.ok(sessionService.getAllSessionsByUserId(userId,request));

    }

    @Operation(summary = "세션 단일 조회", description = "세션 ID를 기반으로 단일 세션을 조회합니다.")
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSessionBySessionId(@PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionService.getSessionBySessionId(sessionId));
    }

    @Operation(summary = "세션 게시물 삭제", description = "세션 게시물을 삭제합니다.")
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId,
            @Parameter(hidden = true) @UserId Long userId
    ) {
        sessionService.deleteSession(sessionId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "세션 인기 게시물 목록 조회", description = "세션 인기 게시물 목록을 커서 기반 페이지네이션으로 조회합니다.")
    @GetMapping("/best")
    public ResponseEntity<CursorPageViewCountResponse<SessionResponse>> getAllBestSessions(
            @ParameterObject @Valid CursorPageViewCountRequest request
    ) {
        return ResponseEntity.ok(sessionService.getAllBestSessions(request));
    }
}
