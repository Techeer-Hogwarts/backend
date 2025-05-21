package backend.techeerzip.domain.session.service;

import backend.techeerzip.domain.session.dto.request.SessionCreateRequest;
import backend.techeerzip.domain.session.dto.response.SessionResponse;
import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.domain.session.exception.SessionNotFoundException;
import backend.techeerzip.domain.session.exception.SessionUnauthorizedException;
import backend.techeerzip.domain.session.repository.SessionRepository;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.user.repository.UserRepository;
import backend.techeerzip.global.common.CursorPageViewCountRequest;
import backend.techeerzip.global.common.CursorPageViewCountResponse;
import backend.techeerzip.global.common.CursorPageCreatedAtRequest;
import backend.techeerzip.global.common.CursorPageCreatedAtResponse;
import backend.techeerzip.global.exception.ErrorCode;
import backend.techeerzip.global.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createSession(SessionCreateRequest request, Long userId) {
        // Todo: 유저 검증 삭제 - 유저 검증은 JWT 검증 단계에서 이루어짐
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Session session = new Session(
                request.title(),
                request.thumbnail(),
                request.videoUrl(),
                request.fileUrl(),
                request.presenter(),
                request.date(),
                request.category(),
                request.position(),
                user
        );
        Session savedSession = sessionRepository.save(session);
        return savedSession.getId();
    }

    @Transactional
    public void updateSession(SessionCreateRequest request, Long sessionId, Long userId) {
        // Todo: 유저 검증 삭제 - 유저 검증은 JWT 검증 단계에서 이루어짐
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

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
    public CursorPageCreatedAtResponse<SessionResponse> getAllSessions(CursorPageCreatedAtRequest request) {
        CursorPageCreatedAtResponse<Session> page = sessionRepository.findAllByCursor(request);
        List<SessionResponse> sessionResponses = page.content().stream()
                .map(SessionResponse::from)
                .toList();
        return new CursorPageCreatedAtResponse<>(sessionResponses, page.nextCursor(), page.nextCreatedAt(), page.hasNext());
    }



    public CursorPageCreatedAtResponse<SessionResponse> getAllSessionsByUserId(Long userId, CursorPageCreatedAtRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        CursorPageCreatedAtResponse<Session> page = sessionRepository.findAllByUserIdCursor(userId, request);
        List<SessionResponse> sessionResponses = page.content().stream()
                .map(SessionResponse::from)
                .toList();
        return new CursorPageCreatedAtResponse<>(sessionResponses, page.nextCursor(), page.nextCreatedAt(), page.hasNext());
    }

    public SessionResponse getSessionBySessionId(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(SessionNotFoundException::new);
        return SessionResponse.from(session);
    }

    @Transactional
    public void deleteSession(Long sessionId, Long userId) {
        // Todo: 유저 검증 삭제 - 유저 검증은 JWT 검증 단계에서 이루어짐
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(SessionNotFoundException::new);
        validateSessionAuthor(userId, session);
        sessionRepository.deleteById(sessionId);
    }

    @Transactional
    public CursorPageViewCountResponse<SessionResponse> getAllBestSessions(CursorPageViewCountRequest request) {
        CursorPageViewCountResponse<Session> page = sessionRepository.findAllBestSessionsByCursor(request);
        List<SessionResponse> sessionResponses = page.content().stream()
                .map(SessionResponse::from)
                .toList();
        return new CursorPageViewCountResponse<>(
                sessionResponses,
                page.nextCursor(),
                page.nextCreatedAt(),
                page.nextViewCount(),
                page.hasNext()
        );
    }

    private void validateSessionAuthor(Long userId, Session session) {
        if (!userId.equals(session.getUser().getId())) throw new SessionUnauthorizedException();
    }
}
