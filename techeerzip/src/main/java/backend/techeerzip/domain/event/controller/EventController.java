package backend.techeerzip.domain.event.controller;

import backend.techeerzip.domain.event.dto.request.EventCreateRequest;
import backend.techeerzip.domain.event.dto.request.EventListQueryRequest;
import backend.techeerzip.domain.event.dto.response.EventCreateResponse;
import backend.techeerzip.domain.event.dto.response.EventListResponse;
import backend.techeerzip.domain.event.dto.response.EventResponse;
import backend.techeerzip.domain.event.service.EventService;
import backend.techeerzip.global.resolver.UserId;
import backend.techeerzip.global.logger.CustomLogger;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v3/events")
@RequiredArgsConstructor
@Tag(name = "events", description = "이벤트 API")
public class EventController implements EventSwagger {

    private final EventService eventService;
    private final CustomLogger logger;
    private static final String CONTEXT = "EventController";

    @Override
    @PostMapping
    public ResponseEntity<EventCreateResponse> createEvent(
            @Valid @RequestBody EventCreateRequest request,
            @UserId Long userId) {
        logger.debug("이벤트 생성 요청 처리 중 - userId: {} | context: {}", userId, CONTEXT);
        EventCreateResponse response = eventService.createEvent(userId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<EventListResponse> getEventList(
            @ParameterObject @Valid EventListQueryRequest query) {
        logger.debug("이벤트 목록 조회 및 검색 처리 중 - query: {} | context: {}", query, CONTEXT);
        EventListResponse response = eventService.getEventList(query);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long eventId) {
        logger.debug("단일 이벤트 조회 처리 중 - eventId: {} | context: {}", eventId, CONTEXT);
        EventResponse response = eventService.getEvent(eventId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventCreateResponse> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventCreateRequest request,
            @UserId Long userId) {
        logger.debug("이벤트 수정 요청 처리 중 - userId: {}, eventId: {} | context: {}", userId, eventId, CONTEXT);
        EventCreateResponse response = eventService.updateEvent(userId, eventId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long eventId,
            @UserId Long userId) {
        logger.debug("이벤트 삭제 요청 처리 중 - userId: {}, eventId: {} | context: {}", userId, eventId, CONTEXT);
        eventService.deleteEvent(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}