package backend.techeerzip.domain.session.repository;

import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.global.common.CursorPageCreatedAtRequest;
import backend.techeerzip.global.common.CursorPageCreatedAtResponse;

public interface SessionDSLRepository {
    CursorPageCreatedAtResponse<Session> findAllByCursor(CursorPageCreatedAtRequest request);

    CursorPageCreatedAtResponse<Session> getAllSessionsByUserId(Long userId, CursorPageCreatedAtRequest request);
}
