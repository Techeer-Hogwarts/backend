package backend.techeerzip.domain.techBloggingChallenge.repository;

import java.util.List;

import backend.techeerzip.domain.blog.entity.Blog;

public interface TechBloggingAttendanceRepositoryCustom {
    /**
     * 회차별 블로그 커서 기반 조회
     *
     * @param roundId 회차 ID
     * @param termId 챌린지 기간(분기) ID (nullable)
     * @param sort 정렬 옵션(latest, view, name)
     * @param cursorBlog 커서 블로그 (null 가능)
     * @param size 페이지 크기
     * @return 블로그 목록
     */
    List<Blog> findBlogsByRoundWithCursor(
            Long roundId, Long termId, String sort, Blog cursorBlog, int size);

    List<Long> getValidBlogIds(Long termId, Long roundId);
}
