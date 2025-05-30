package backend.techeerzip.domain.event.controller;

import backend.techeerzip.domain.event.dto.request.EventCreateRequest;
import backend.techeerzip.domain.event.dto.request.EventListQueryRequest;
import backend.techeerzip.domain.event.dto.response.EventCreateResponse;
import backend.techeerzip.domain.event.dto.response.EventGetResponse;
import backend.techeerzip.domain.event.service.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v3/events")
@RequiredArgsConstructor
@Tag(name = "events", description = "이벤트 API")
public class EventController implements EventSwagger {

    private final EventService eventService;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Override
    @PostMapping
    public ResponseEntity<EventCreateResponse> createEvent(
            @Valid @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId) {
        logger.debug("이벤트 생성 요청 처리 중 - userId: {}", userId);
        EventCreateResponse response = eventService.createEvent(userId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<EventGetResponse>> getEventList(
            @ParameterObject @Valid EventListQueryRequest query) {
        logger.debug("이벤트 목록 조회 및 검색 처리 중 - query: {}", query);
        List<EventGetResponse> responseList = eventService.getEventList(query);
        return ResponseEntity.ok(responseList);
    }

    @Override
    @GetMapping("/{eventId}")
    public ResponseEntity<EventGetResponse> getEvent(@PathVariable Long eventId) {
        logger.debug("단일 이벤트 조회 처리 중 - eventId: {}", eventId);
        EventGetResponse response = eventService.getEvent(eventId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventCreateResponse> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId) {
        logger.debug("이벤트 수정 요청 처리 중 - userId: {}, eventId: {}", userId, eventId);
        EventCreateResponse response = eventService.updateEvent(userId, eventId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal(expression = "id") Long userId) {
        logger.debug("이벤트 삭제 요청 처리 중 - userId: {}, eventId: {}", userId, eventId);
        eventService.deleteEvent(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}
