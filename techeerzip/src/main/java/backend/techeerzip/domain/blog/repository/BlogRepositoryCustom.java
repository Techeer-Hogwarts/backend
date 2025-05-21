package backend.techeerzip.domain.blog.repository;

import java.util.List;

import backend.techeerzip.domain.blog.entity.Blog;

public interface BlogRepositoryCustom {
    /**
     * 커서 기반으로 블로그 목록을 조회합니다.
     *
     * @param cursorId 마지막으로 조회한 블로그의 ID
     * @param category 블로그 카테고리
     * @param sortBy 정렬 기준 (latest/popular/name)
     * @param limit 조회할 개수
     * @return 블로그 목록
     */
    List<Blog> findBlogsWithCursor(Long cursorId, String category, String sortBy, int limit);

    /**
     * 특정 유저의 블로그를 커서 기반으로 조회합니다.
     *
     * @param userId 유저 ID
     * @param cursorId 마지막으로 조회한 블로그의 ID
     * @param limit 조회할 개수
     * @return 블로그 목록
     */
    List<Blog> findUserBlogsWithCursor(Long userId, Long cursorId, int limit);

    /**
     * 인기 블로그를 커서 기반으로 조회합니다.
     *
     * @param cursorId 마지막으로 조회한 블로그의 ID
     * @param limit 조회할 개수
     * @return 블로그 목록
     */
    List<Blog> findPopularBlogsWithCursor(Long cursorId, int limit);
}
