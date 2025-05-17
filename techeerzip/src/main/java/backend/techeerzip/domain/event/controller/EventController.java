package backend.techeerzip.domain.event.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.event.dto.EventDto;
import backend.techeerzip.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v3/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto.Response> createEvent(
            @Valid @RequestBody EventDto.Create request,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        // TODO: Implement event creation
        EventDto.Response response = eventService.createEvent(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<EventDto.Response>> getAllEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> category,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        List<EventDto.Response> events = eventService.getEventList(keyword, category, offset, limit);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto.Response> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEvent(eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventDto.Response> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventDto.Create request,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        return ResponseEntity.ok(eventService.updateEvent(userId, eventId, request));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<EventDto.Response> deleteEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        eventService.deleteEvent(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}
