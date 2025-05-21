package backend.techeerzip.domain.session.repository;

import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.global.common.CursorPageViewCountRequest;
import backend.techeerzip.global.common.CursorPageViewCountResponse;
import backend.techeerzip.global.common.CursorPageCreatedAtRequest;
import backend.techeerzip.global.common.CursorPageCreatedAtResponse;

public interface SessionDSLRepository {
    CursorPageCreatedAtResponse<Session> findAllByCursor(CursorPageCreatedAtRequest request);

    CursorPageCreatedAtResponse<Session> findAllByUserIdCursor(Long userId, CursorPageCreatedAtRequest request);

    CursorPageViewCountResponse<Session> findAllBestSessionsByCursor(CursorPageViewCountRequest request);
}
