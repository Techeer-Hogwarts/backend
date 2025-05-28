package backend.techeerzip.domain.session.controller;

import backend.techeerzip.domain.session.dto.request.SessionCreateRequest;
import backend.techeerzip.domain.session.dto.request.SessionListQueryRequest;
import backend.techeerzip.domain.session.dto.response.SessionResponse;
import backend.techeerzip.domain.session.service.SessionService;
import backend.techeerzip.domain.session.dto.request.SessionBestListRequest;
import backend.techeerzip.domain.session.dto.response.SessionBestListResponse;
import backend.techeerzip.domain.session.dto.response.SessionListResponse;
import backend.techeerzip.global.resolver.UserId;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/sessions")
@RequiredArgsConstructor
public class SessionController implements SessionSwagger {
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<Long> createSession(
            @RequestBody @Valid SessionCreateRequest request,
            @UserId Long userId
    ) {
        return ResponseEntity.ok(sessionService.createSession(request, userId));
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<Void> updateSession(
            @RequestBody @Valid SessionCreateRequest request,
            @PathVariable Long sessionId,
            @UserId Long userId
    ) {
        sessionService.updateSession(request, sessionId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSessionBySessionId(@PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionService.getSessionBySessionId(sessionId));
    }

    @GetMapping
    public ResponseEntity<SessionListResponse<SessionResponse>> getAllSessions(
            @ParameterObject @Valid SessionListQueryRequest request) {
        return ResponseEntity.ok(sessionService.getAllSessions(request));
    }

    @GetMapping("/best")
    public ResponseEntity<SessionBestListResponse<SessionResponse>> getAllBestSessions(
            @ParameterObject @Valid SessionBestListRequest request
    ) {
        return ResponseEntity.ok(sessionService.getAllBestSessions(request));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId,
            @UserId Long userId
    ) {
        sessionService.deleteSession(sessionId, userId);
        return ResponseEntity.ok().build();
    }
}
