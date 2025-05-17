package backend.techeerzip.domain.event.controller;

import backend.techeerzip.domain.event.dto.request.CreateEventRequest;
import backend.techeerzip.domain.event.dto.request.GetEventListQueryRequest;
import backend.techeerzip.domain.event.dto.response.CreateEventResponse;
import backend.techeerzip.domain.event.dto.response.GetEventResponse;
import backend.techeerzip.domain.event.service.EventService;
import backend.techeerzip.global.logger.CustomLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "events", description = "이벤트 API")
@RestController
@RequestMapping("/api/v3/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final CustomLogger logger;

    @Operation(summary = "이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    @PostMapping
    public ResponseEntity<CreateEventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId) {
        logger.debug("이벤트 생성 요청 처리 중 - userId: " + userId, EventController.class.getSimpleName());
        CreateEventResponse response = eventService.createEvent(userId, request);
        logger.debug("이벤트 생성 요청 처리 완료", EventController.class.getSimpleName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이벤트 목록 조회 및 검색", description = "이벤트 목록을 조회하고 검색합니다.")
    @GetMapping
    public ResponseEntity<List<GetEventResponse>> getEventList(
            @Parameter(description = "검색할 키워드") @RequestParam(required = false) String keyword,
            @Parameter(description = "카테고리") @RequestParam(required = false) List<String> category,
            @Parameter(description = "오프셋") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "가져올 개수") @RequestParam(defaultValue = "10") int limit) {
        GetEventListQueryRequest query = new GetEventListQueryRequest(keyword, category, offset, limit);
        logger.debug("이벤트 목록 조회 및 검색 처리 중 - query: " + query, EventController.class.getSimpleName());
        List<GetEventResponse> responses = eventService.getEventList(query);
        logger.debug("이벤트 목록 조회 및 검색 처리 완료", EventController.class.getSimpleName());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "단일 이벤트 조회", description = "지정된 ID의 이벤트를 조회합니다.")
    @GetMapping("/{eventId}")
    public ResponseEntity<GetEventResponse> getEvent(@PathVariable Long eventId) {
        logger.debug("단일 이벤트 조회 처리 중 - eventId: " + eventId, EventController.class.getSimpleName());
        GetEventResponse response = eventService.getEvent(eventId);
        logger.debug("단일 이벤트 목록 조회 처리 완료", EventController.class.getSimpleName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이벤트 수정", description = "지정된 ID의 이벤트를 수정합니다.")
    @PatchMapping("/{eventId}")
    public ResponseEntity<CreateEventResponse> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId) {
        logger.debug("이벤트 수정 요청 처리 중 - userId: " + userId + ", eventId: " + eventId, EventController.class.getSimpleName());
        CreateEventResponse response = eventService.updateEvent(userId, eventId, request);
        logger.debug("이벤트 수정 요청 처리 완료", EventController.class.getSimpleName());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이벤트 삭제", description = "지정된 ID의 이벤트를 삭제합니다.")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal(expression = "id") Long userId) {
        logger.debug("이벤트 삭제 요청 처리 중 - userId: " + userId + ", eventId: " + eventId, EventController.class.getSimpleName());
        eventService.deleteEvent(userId, eventId);
        logger.debug("이벤트 삭제 요청 처리 완료", EventController.class.getSimpleName());
        return ResponseEntity.noContent().build();
    }
}
