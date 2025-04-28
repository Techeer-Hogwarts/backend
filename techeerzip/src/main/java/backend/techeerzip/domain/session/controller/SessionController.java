package backend.techeerzip.domain.session.controller;

import backend.techeerzip.domain.session.dto.SessionDto;
import backend.techeerzip.domain.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionDto.Response> createSession(@RequestBody SessionDto.Create request) {
        // TODO: Implement session creation
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        // TODO: Implement session deletion
        return ResponseEntity.ok().build();
    }
} 