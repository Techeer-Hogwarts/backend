package backend.techeerzip.domain.session.repository;

import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.domain.session.dto.request.CursorPageViewCountRequest;
import backend.techeerzip.domain.session.dto.response.CursorPageViewCountResponse;
import backend.techeerzip.domain.session.dto.request.CursorPageCreatedAtRequest;
import backend.techeerzip.domain.session.dto.response.CursorPageCreatedAtResponse;

public interface SessionDSLRepository {
    CursorPageCreatedAtResponse<Session> findAllByCursor(CursorPageCreatedAtRequest request);

    CursorPageCreatedAtResponse<Session> findAllByUserIdCursor(Long userId, CursorPageCreatedAtRequest request);

    CursorPageViewCountResponse<Session> findAllBestSessionsByCursor(CursorPageViewCountRequest request);
}
