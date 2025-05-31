package backend.techeerzip.domain.session.mapper;

import backend.techeerzip.domain.session.dto.request.SessionIndexRequest;
import backend.techeerzip.domain.session.entity.Session;

public class SessionMapper {
    private SessionMapper() {
        throw new IllegalStateException("SessionMapper is a utility class");
    }

    public static SessionIndexRequest toIndexDto(Session session) {
        return new SessionIndexRequest(
                session.getDate(),
                String.valueOf(session.getId()),
                String.valueOf(session.getLikeCount()),
                session.getPresenter(),
                session.getThumbnail(),
                session.getTitle(),
                String.valueOf(session.getViewCount()),
                session.getVideoUrl(),
                session.getFileUrl()
        );
    }
} 