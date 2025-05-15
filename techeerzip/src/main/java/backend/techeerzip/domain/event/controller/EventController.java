package backend.techeerzip.domain.event.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import backend.techeerzip.domain.event.dto.EventDto;
import backend.techeerzip.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<?> getAllEvents() {
        // TODO: Implement getting all events
        return ResponseEntity.ok().build();
    }
}
