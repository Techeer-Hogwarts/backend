package backend.techeerzip.domain.event.service;

import backend.techeerzip.domain.event.dto.request.CreateEventRequest;
import backend.techeerzip.domain.event.dto.request.GetEventListQueryRequest;
import backend.techeerzip.domain.event.dto.response.CreateEventResponse;
import backend.techeerzip.domain.event.dto.response.GetEventResponse;
import backend.techeerzip.domain.event.entity.Event;
import backend.techeerzip.domain.event.exception.EventException;
import backend.techeerzip.domain.event.repository.EventRepository;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public CreateEventResponse createEvent(Long userId, CreateEventRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EventException(ErrorCode.USER_NOT_FOUND));
        Event event = new Event(
                request.getCategory(),
                request.getTitle(),
                request.getStartDate(),
                request.getEndDate(),
                request.getUrl(),
                user
        );
        Event savedEvent = eventRepository.save(event);
        return new CreateEventResponse(savedEvent);
    }

    public List<GetEventResponse> getEventList(GetEventListQueryRequest query) {
        Pageable pageable = PageRequest.of(query.getOffset(), query.getLimit());
        List<Event> events = eventRepository.findByFilters(
                query.getKeyword(),
                query.getCategory(),
                pageable
        );
        return events.stream()
                .map(GetEventResponse::new)
                .collect(Collectors.toList());
    }

    public GetEventResponse getEvent(Long eventId) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));
        return new GetEventResponse(event);
    }

    public CreateEventResponse updateEvent(Long userId, Long eventId, CreateEventRequest request) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));
        if (!event.getUser().getId().equals(userId)) {
            throw new EventException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        event.update(
                request.getCategory(),
                request.getTitle(),
                request.getStartDate(),
                request.getEndDate(),
                request.getUrl()
        );
        Event updatedEvent = eventRepository.save(event);
        return new CreateEventResponse(updatedEvent);
    }

    public void deleteEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));
        if (!event.getUser().getId().equals(userId)) {
            throw new EventException(ErrorCode.HANDLE_ACCESS_DENIED);
        }
        event.delete();
        eventRepository.save(event);
    }
}
