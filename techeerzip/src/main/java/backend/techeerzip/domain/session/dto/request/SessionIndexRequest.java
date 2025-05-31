package backend.techeerzip.domain.session.dto.request;

import backend.techeerzip.domain.session.entity.Session;

public record SessionIndexRequest(
    String date,
    String id,
    String likeCount,
    String presenter,
    String thumbnail,
    String title,
    String viewCount,
    String videoUrl,
    String fileUrl
) {
    public SessionIndexRequest(Session session) {
        this(
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
