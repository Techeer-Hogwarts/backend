package backend.techeerzip.domain.event.service;

import backend.techeerzip.domain.event.dto.EventDto;
import backend.techeerzip.domain.event.entity.Event;
import backend.techeerzip.domain.event.exception.EventException;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    // TODO: 필요한 서비스 메서드 구현
    @Transactional
    public EventDto.Response createEvent(Long userId, EventDto.Create request) {
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
        Event saved = eventRepository.save(event);
        return toResponse(saved);
    }

    public List<EventDto.Response> getEventList(String keyword, List<String> category, int offset, int limit) {
        List<Event> events = eventRepository.findByIsDeletedFalseOrderByStartDateDesc();
        return events.stream()
                .filter(e -> (keyword == null || e.getTitle().contains(keyword)) &&
                        (category == null || category.contains(e.getCategory())))
                .skip(offset)
                .limit(limit)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EventDto.Response getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));
        return toResponse(event);
    }

    @Transactional
    public EventDto.Response updateEvent(Long userId, Long eventId, EventDto.Create request) {
        Event event = eventRepository.findById(eventId)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));

        if (!event.getUser().getId().equals(userId)) {
            throw new EventException(ErrorCode.HANDLE_ACCESS_DENIED);
        }

        event.update(request.getCategory(), request.getTitle(), request.getStartDate(), request.getEndDate(), request.getUrl());
        return toResponse(event);
    }

    @Transactional
    public void deleteEvent(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));

        if (!event.getUser().getId().equals(userId)) {
            throw new EventException(ErrorCode.HANDLE_ACCESS_DENIED);
        }

        event.delete();
    }

    private EventDto.Response toResponse(Event event) {
        return new EventDto.Response(
                event.getId(),
                event.getUser().getId(),
                event.getCategory(),
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                event.getUrl(),
                new EventDto.Response(UserDto(
                        event.getUser().getName(),
                        event.getUser().getNickname(),
                        event.getUser().getProfileImage()
                )
        );
    }
}
