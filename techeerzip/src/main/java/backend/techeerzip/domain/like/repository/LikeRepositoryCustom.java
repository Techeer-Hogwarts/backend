package backend.techeerzip.domain.like.repository;

import java.util.List;

import backend.techeerzip.domain.like.entity.Like;

public interface LikeRepositoryCustom {
    /**
     * 커서 기반으로 사용자의 좋아요 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param category 좋아요 카테고리
     * @param cursorId 마지막으로 조회한 좋아요의 contentId
     * @param limit 조회할 개수
     * @return 좋아요 목록
     */
    List<Like> findLikesWithCursor(Long userId, String category, Long cursorId, int limit);
}
