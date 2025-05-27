package backend.techeerzip.domain.session.service;

import backend.techeerzip.domain.session.dto.request.SessionCreateRequest;
import backend.techeerzip.domain.session.dto.request.SessionListQueryRequest;
import backend.techeerzip.domain.session.dto.response.SessionResponse;
import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.domain.session.exception.SessionNotFoundException;
import backend.techeerzip.domain.session.exception.SessionUnauthorizedException;
import backend.techeerzip.domain.session.repository.SessionRepository;
import backend.techeerzip.domain.session.dto.request.SessionBestListRequest;
import backend.techeerzip.domain.session.dto.response.SessionBestListResponse;
import backend.techeerzip.domain.session.dto.response.SessionListResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {
    private final SessionRepository sessionRepository;

    @Transactional
    public Long createSession(SessionCreateRequest request, Long userId) {
        Session session = new Session(
                request.title(),
                request.thumbnail(),
                request.videoUrl(),
                request.fileUrl(),
                request.presenter(),
                request.date(),
                request.category(),
                request.position(),
                userId
        );
        Session savedSession = sessionRepository.save(session);
        return savedSession.getId();
    }

    @Transactional
    public void updateSession(SessionCreateRequest request, Long sessionId, Long userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(SessionNotFoundException::new);
        validateSessionAuthor(userId, session);
        session.update(
                request.title(),
                request.thumbnail(),
                request.videoUrl(),
                request.fileUrl(),
                request.presenter(),
                request.date(),
                request.category(),
                request.position()
        );
        sessionRepository.save(session);
    }

    @Transactional
    public SessionListResponse<SessionResponse> getAllSessions(SessionListQueryRequest request) {
        SessionListResponse<Session> page = sessionRepository.findAllByCursor(request);
        List<SessionResponse> sessionResponses = page.content().stream()
                .map(SessionResponse::from)
                .toList();
        return new SessionListResponse<>(sessionResponses, page.nextCursor(), page.nextCreatedAt(), page.hasNext());
    }



    public SessionListResponse<SessionResponse> getAllSessionsByUserId(Long userId, SessionListQueryRequest request) {
        SessionListResponse<Session> page = sessionRepository.findAllByUserIdCursor(userId, request);
        List<SessionResponse> sessionResponses = page.content().stream()
                .map(SessionResponse::from)
                .toList();
        return new SessionListResponse<>(sessionResponses, page.nextCursor(), page.nextCreatedAt(), page.hasNext());
    }

    public SessionResponse getSessionBySessionId(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(SessionNotFoundException::new);
        return SessionResponse.from(session);
    }

    @Transactional
    public void deleteSession(Long sessionId, Long userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(SessionNotFoundException::new);
        validateSessionAuthor(userId, session);
        sessionRepository.deleteById(sessionId);
    }

    @Transactional
    public SessionBestListResponse<SessionResponse> getAllBestSessions(SessionBestListRequest request) {
        SessionBestListResponse<Session> page = sessionRepository.findAllBestSessionsByCursor(request);
        List<SessionResponse> sessionResponses = page.content().stream()
                .map(SessionResponse::from)
                .toList();
        return new SessionBestListResponse<>(
                sessionResponses,
                page.nextCursor(),
                page.nextCreatedAt(),
                page.nextViewCount(),
                page.hasNext()
        );
    }

    private void validateSessionAuthor(Long userId, Session session) {
        if (!userId.equals(session.getUserId())) throw new SessionUnauthorizedException();
    }
}
