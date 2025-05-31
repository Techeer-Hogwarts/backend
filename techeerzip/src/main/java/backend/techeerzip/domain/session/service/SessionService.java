package backend.techeerzip.domain.session.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.techeerzip.domain.session.dto.request.SessionBestListRequest;
import backend.techeerzip.domain.session.dto.request.SessionCreateRequest;
import backend.techeerzip.domain.session.dto.request.SessionListQueryRequest;
import backend.techeerzip.domain.session.dto.response.SessionBestListResponse;
import backend.techeerzip.domain.session.dto.response.SessionListResponse;
import backend.techeerzip.domain.session.dto.response.SessionResponse;
import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.domain.session.exception.SessionNotFoundException;
import backend.techeerzip.domain.session.exception.SessionUnauthorizedException;
import backend.techeerzip.domain.session.mapper.SessionMapper;
import backend.techeerzip.domain.session.repository.SessionRepository;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.exception.UserNotFoundException;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.infra.index.IndexEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long createSession(SessionCreateRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Session session =
                new Session(
                        request.title(),
                        request.thumbnail(),
                        request.videoUrl(),
                        request.fileUrl(),
                        request.presenter(),
                        request.date(),
                        request.category(),
                        request.position(),
                        user);
        Session savedSession = sessionRepository.save(session);
        eventPublisher.publishEvent(
                new IndexEvent.Create<>("session", SessionMapper.toIndexDto(savedSession)));
        return savedSession.getId();
    }

    @Transactional
    public Session updateSession(SessionCreateRequest request, Long sessionId, Long userId) {
        Session session =
                sessionRepository.findById(sessionId).orElseThrow(SessionNotFoundException::new);
        validateSessionAuthor(userId, session);
        session.update(
                request.title(),
                request.thumbnail(),
                request.videoUrl(),
                request.fileUrl(),
                request.presenter(),
                request.date(),
                request.category(),
                request.position());
        return session;
    }

    public SessionListResponse<SessionResponse> getAllSessions(SessionListQueryRequest request) {
        SessionListResponse<Session> page = sessionRepository.findAllByCursor(request);
        List<SessionResponse> sessionResponses =
                page.content().stream().map(SessionResponse::from).toList();
        return new SessionListResponse<>(
                sessionResponses, page.nextCursor(), page.nextCreatedAt(), page.hasNext());
    }

    public SessionResponse getSessionBySessionId(Long sessionId) {
        Session session =
                sessionRepository.findById(sessionId).orElseThrow(SessionNotFoundException::new);
        return SessionResponse.from(session);
    }

    @Transactional
    public void deleteSession(Long sessionId, Long userId) {
        Session session =
                sessionRepository.findById(sessionId).orElseThrow(SessionNotFoundException::new);
        validateSessionAuthor(userId, session);
        sessionRepository.deleteById(sessionId);
        eventPublisher.publishEvent(new IndexEvent.Delete("session", sessionId));
    }

    public SessionBestListResponse<SessionResponse> getAllBestSessions(
            SessionBestListRequest request) {
        SessionBestListResponse<Session> page =
                sessionRepository.findAllBestSessionsByCursor(request);
        List<SessionResponse> sessionResponses =
                page.content().stream().map(SessionResponse::from).toList();
        return new SessionBestListResponse<>(
                sessionResponses,
                page.nextCursor(),
                page.nextCreatedAt(),
                page.nextViewCount(),
                page.hasNext());
    }

    private void validateSessionAuthor(Long userId, Session session) {
        if (!userId.equals(session.getUser().getId())) throw new SessionUnauthorizedException();
    }
}
