package backend.techeerzip.domain.event.repository;

import java.util.List;

import backend.techeerzip.domain.event.entity.Event;

public interface EventRepositoryCustom {
    /**
     * 커서 기반으로 이벤트 목록을 조회합니다.
     *
     * @param cursorId 마지막으로 조회한 이벤트의 ID
     * @param keyword 검색할 키워드
     * @param categories 필터링할 카테고리 목록
     * @param limit 조회할 개수
     * @return 이벤트 목록
     */
    List<Event> findEventsWithCursor(
            Long cursorId, String keyword, List<String> categories, int limit);
}
