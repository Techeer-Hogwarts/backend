package backend.techeerzip.domain.blog.repository;

import java.util.List;

import backend.techeerzip.domain.blog.entity.Blog;

public interface BlogRepositoryCustom {
    /**
     * 커서 기반으로 블로그 목록을 조회합니다.
     *
     * @param cursorId 마지막으로 조회한 블로그의 ID
     * @param category 블로그 카테고리
     * @param sortBy 정렬 기준 (latest/viewCount/name)
     * @param limit 조회할 개수
     * @return 블로그 목록
     */
    List<Blog> findBlogsWithCursor(Long cursorId, String category, String sortBy, int limit);

    /**
     * 인기 블로그를 커서 기반으로 조회합니다.
     *
     * @param cursorId 마지막으로 조회한 블로그의 ID
     * @param limit 조회할 개수
     * @return 블로그 목록
     */
    List<Blog> findPopularBlogsWithCursor(Long cursorId, int limit);

    /**
     * 챌린지용 블로그 목록을 최신순으로 조회합니다.
     *
     * @param blogIds 조회할 블로그 ID 목록
     * @param cursorBlog 커서 블로그
     * @param limit 조회할 개수
     * @param sortBy 정렬 기준 (latest/viewCount/name)
     * @return 블로그 목록
     */
    List<Blog> findBlogsForChallenge(List<Long> blogIds, Blog cursorBlog, int limit, String sortBy);
}
