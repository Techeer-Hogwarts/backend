package backend.techeerzip.domain.event.service;

import backend.techeerzip.domain.event.dto.request.CreateEventRequest;
import backend.techeerzip.domain.event.dto.request.GetEventListQueryRequest;
import backend.techeerzip.domain.event.dto.response.CreateEventResponse;
import backend.techeerzip.domain.event.dto.response.GetEventResponse;
import backend.techeerzip.domain.event.entity.Event;
import backend.techeerzip.domain.event.exception.EventException;
import backend.techeerzip.domain.event.dto.EventDto;
import backend.techeerzip.domain.event.entity.Event;
import backend.techeerzip.domain.event.exception.EventException;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import backend.techeerzip.domain.event.repository.EventRepository;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.exception.ErrorCode;
import backend.techeerzip.global.logger.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CustomLogger logger;
    private static final String CONTEXT = "EventService";

    public CreateEventResponse createEvent(Long userId, CreateEventRequest request) {
        logger.debug(String.format("이벤트 생성 시작 - userId: %d, request: %s", userId, request), CONTEXT);

        try {
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

            logger.debug(String.format("이벤트 생성 완료 - eventId: %d", savedEvent.getId()), CONTEXT);
            return new CreateEventResponse(savedEvent);
        } catch (Exception e) {
            logger.error(String.format("이벤트 생성 실패 - error: %s", e.getMessage()), CONTEXT);
            throw e;
        }
    }

    public List<GetEventResponse> getEventList(GetEventListQueryRequest query) {
        logger.debug(String.format("이벤트 목록 조회 시작 - query: %s", query), CONTEXT);

        try {
            Pageable pageable = PageRequest.of(query.getOffset(), query.getLimit());
            List<Event> events = eventRepository.findByFilters(
                    query.getKeyword(),
                    query.getCategory(),
                    pageable
            );

            logger.debug(String.format("이벤트 목록 조회 완료 - 조회된 개수: %d", events.size()), CONTEXT);
            return events.stream()
                    .map(GetEventResponse::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(String.format("이벤트 목록 조회 실패 - error: %s", e.getMessage()), CONTEXT);
            throw e;
        }
    }

    public GetEventResponse getEvent(Long eventId) {
        logger.debug(String.format("단일 이벤트 조회 시작 - eventId: %d", eventId), CONTEXT);

        try {
            Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                    .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));

            logger.debug(String.format("단일 이벤트 조회 완료 - eventId: %d", eventId), CONTEXT);
            return new GetEventResponse(event);
        } catch (Exception e) {
            logger.error(String.format("단일 이벤트 조회 실패 - eventId: %d, error: %s", eventId, e.getMessage()), CONTEXT);
            throw e;
        }
    }

    public CreateEventResponse updateEvent(Long userId, Long eventId, CreateEventRequest request) {
        logger.debug(String.format("이벤트 수정 시작 - userId: %d, eventId: %d, request: %s", userId, eventId, request), CONTEXT);

        try {
            Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                    .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));

            if (!event.getUser().getId().equals(userId)) {
                logger.error(String.format("이벤트 수정 권한 없음 - userId: %d, eventId: %d", userId, eventId), CONTEXT);
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

            logger.debug(String.format("이벤트 수정 완료 - eventId: %d", eventId), CONTEXT);
            return new CreateEventResponse(updatedEvent);
        } catch (EventException e) {
            throw e;
        } catch (Exception e) {
            logger.error(String.format("이벤트 수정 실패 - eventId: %d, error: %s", eventId, e.getMessage()), CONTEXT);
            throw e;
        }
    }

    public void deleteEvent(Long userId, Long eventId) {
        logger.debug(String.format("이벤트 삭제 시작 - userId: %d, eventId: %d", userId, eventId), CONTEXT);

        try {
            Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
                    .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));

            if (!event.getUser().getId().equals(userId)) {
                logger.error(String.format("이벤트 삭제 권한 없음 - userId: %d, eventId: %d", userId, eventId), CONTEXT);
                throw new EventException(ErrorCode.HANDLE_ACCESS_DENIED);
            }

            event.delete();
            eventRepository.save(event);

            logger.debug(String.format("이벤트 삭제 완료 - eventId: %d", eventId), CONTEXT);
        } catch (EventException e) {
            throw e;
        } catch (Exception e) {
            logger.error(String.format("이벤트 삭제 실패 - eventId: %d, error: %s", eventId, e.getMessage()), CONTEXT);
            throw e;
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
                new EventDto.Response.UserDto(
                        event.getUser().getName(),
                        event.getUser().getNickname(),
                        event.getUser().getProfileImage()
                )
        );
    }
}
