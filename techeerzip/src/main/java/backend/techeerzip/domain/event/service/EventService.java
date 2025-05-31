package backend.techeerzip.domain.event.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.event.dto.request.EventCreateRequest;
import backend.techeerzip.domain.event.dto.request.EventListQueryRequest;
import backend.techeerzip.domain.event.dto.response.EventCreateResponse;
import backend.techeerzip.domain.event.dto.response.EventResponse;
import backend.techeerzip.domain.event.entity.Event;
import backend.techeerzip.domain.event.exception.EventNotFoundException;
import backend.techeerzip.domain.event.exception.EventUnauthorizedException;
import backend.techeerzip.domain.event.mapper.EventMapper;
import backend.techeerzip.domain.event.repository.EventRepository;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.exception.UserNotFoundException;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.infra.index.IndexEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Transactional
    public EventCreateResponse createEvent(Long userId, EventCreateRequest request) {
        logger.debug("이벤트 생성 시작 - userId: {}, request: {}", userId, request);

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> {
                                    logger.warn("이벤트 생성 실패: 사용자를 찾을 수 없습니다 - userId: {}", userId);
                                    return new UserNotFoundException();
                                });

        Event event =
                new Event(
                        request.getCategory(),
                        request.getTitle(),
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getUrl(),
                        user);

        Event savedEvent = eventRepository.save(event);

        eventPublisher.publishEvent(
                new IndexEvent.Create<>("event", EventMapper.toIndexDto(savedEvent)));

        logger.debug("이벤트 생성 완료 - eventId: {}", savedEvent.getId());
        return new EventCreateResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventList(EventListQueryRequest query) {
        logger.debug("이벤트 목록 조회 시작 - query: {}", query);

        List<Event> events =
                eventRepository.findEventsWithCursor(
                        query.getCursorId(),
                        query.getKeyword(),
                        query.getCategory(),
                        query.getLimit() + 1);

        boolean hasNext = events.size() > query.getLimit();
        List<Event> resultEvents = hasNext ? events.subList(0, query.getLimit()) : events;

        List<EventResponse> eventResponses =
                resultEvents.stream().map(EventResponse::new).collect(Collectors.toList());

        logger.debug("이벤트 목록 조회 완료 - 조회된 개수: {}", eventResponses.size());
        return eventResponses;
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(Long eventId) {
        logger.debug("단일 이벤트 조회 시작 - eventId: {}", eventId);

        Event event =
                eventRepository
                        .findByIdAndIsDeletedFalse(eventId)
                        .orElseThrow(
                                () -> {
                                    logger.warn(
                                            "이벤트 조회 실패: 존재하지 않거나 삭제된 이벤트 - eventId: {}", eventId);
                                    return new EventNotFoundException();
                                });

        logger.debug("단일 이벤트 조회 완료 - eventId: {}", eventId);
        return new EventResponse(event);
    }

    @Transactional
    public EventCreateResponse updateEvent(Long userId, Long eventId, EventCreateRequest request) {
        logger.debug("이벤트 수정 시작 - userId: {}, eventId: {}, request: {}", userId, eventId, request);

        Event event =
                eventRepository
                        .findByIdAndIsDeletedFalse(eventId)
                        .orElseThrow(
                                () -> {
                                    logger.warn(
                                            "이벤트 수정 실패: 존재하지 않거나 삭제된 이벤트 - eventId: {}", eventId);
                                    return new EventNotFoundException();
                                });

        if (!event.getUser().getId().equals(userId)) {
            logger.error("이벤트 수정 권한 없음 - userId: {}, eventId: {}", userId, eventId);
            throw new EventUnauthorizedException();
        }

        event.update(
                request.getCategory(),
                request.getTitle(),
                request.getStartDate(),
                request.getEndDate(),
                request.getUrl());

        eventPublisher.publishEvent(
                new IndexEvent.Create<>("event", EventMapper.toIndexDto(event)));

        logger.debug("이벤트 수정 완료 - eventId: {}", eventId);
        return new EventCreateResponse(event);
    }

    @Transactional
    public void deleteEvent(Long userId, Long eventId) {
        logger.debug("이벤트 삭제 시작 - userId: {}, eventId: {}", userId, eventId);

        Event event =
                eventRepository
                        .findByIdAndIsDeletedFalse(eventId)
                        .orElseThrow(
                                () -> {
                                    logger.warn(
                                            "이벤트 삭제 실패: 존재하지 않거나 이미 삭제된 이벤트 - eventId: {}",
                                            eventId);
                                    return new EventNotFoundException();
                                });

        if (!event.getUser().getId().equals(userId)) {
            logger.error("이벤트 삭제 권한 없음 - userId: {}, eventId: {}", userId, eventId);
            throw new EventUnauthorizedException();
        }

        event.delete();
        eventPublisher.publishEvent(new IndexEvent.Delete("event", eventId));

        logger.debug("이벤트 삭제 완료 - eventId: {}", eventId);
    }
}
