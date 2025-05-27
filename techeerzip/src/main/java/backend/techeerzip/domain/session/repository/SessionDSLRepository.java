package backend.techeerzip.domain.session.repository;

import backend.techeerzip.domain.session.dto.request.SessionListQueryRequest;
import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.domain.session.dto.request.SessionBestListRequest;
import backend.techeerzip.domain.session.dto.response.SessionBestListResponse;
import backend.techeerzip.domain.session.dto.response.SessionListResponse;

public interface SessionDSLRepository {
    SessionListResponse<Session> findAllByCursor(SessionListQueryRequest request);

    SessionListResponse<Session> findAllByUserIdCursor(Long userId, SessionListQueryRequest request);

    SessionBestListResponse<Session> findAllBestSessionsByCursor(SessionBestListRequest request);
}
