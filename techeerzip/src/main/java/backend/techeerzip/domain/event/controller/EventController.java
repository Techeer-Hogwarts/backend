package backend.techeerzip.domain.event.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.event.dto.EventDto;
import backend.techeerzip.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Tag(name = "events", description = "이벤트 API")
@RestController
@RequestMapping("/api/v3/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @Operation(summary = "이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    @PostMapping
    public ResponseEntity<EventDto.Response> createEvent(
            @Valid @RequestBody EventDto.Create request,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        EventDto.Response response = eventService.createEvent(userId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이벤트 목록 조회 및 검색", description = "이벤트 목록을 조회하고 검색합니다.")
    @GetMapping
    public ResponseEntity<List<EventDto.Response>> getAllEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> category,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        List<EventDto.Response> events = eventService.getEventList(keyword, category, offset, limit);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "단일 이벤트 조회", description = "지정된 ID의 이벤트를 조회합니다.")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto.Response> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEvent(eventId));
    }

    @Operation(summary = "이벤트 수정", description = "지정된 ID의 이벤트를 수정합니다.")
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventDto.Response> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventDto.Create request,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        return ResponseEntity.ok(eventService.updateEvent(userId, eventId, request));
    }

    @Operation(summary = "이벤트 삭제", description = "지정된 ID의 이벤트를 삭제합니다.")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<EventDto.Response> deleteEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        eventService.deleteEvent(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}
