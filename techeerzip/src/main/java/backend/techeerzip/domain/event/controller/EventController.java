package backend.techeerzip.domain.event.controller;

import backend.techeerzip.domain.event.dto.EventDto;
import backend.techeerzip.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto.Response> createEvent(@RequestBody EventDto.Create request) {
        // TODO: Implement event creation
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllEvents() {
        // TODO: Implement getting all events
        return ResponseEntity.ok().build();
    }
} 