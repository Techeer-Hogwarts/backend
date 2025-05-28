package backend.techeerzip.domain.session.repository;

import backend.techeerzip.domain.session.dto.request.SessionListQueryRequest;
import backend.techeerzip.domain.session.entity.QSession;
import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.domain.session.dto.request.SessionBestListRequest;
import backend.techeerzip.domain.session.dto.response.SessionBestListResponse;
import backend.techeerzip.domain.session.dto.response.SessionListResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SessionDSLRepositoryImpl implements SessionDSLRepository {
    private final JPAQueryFactory queryFactory;
    private final QSession session = QSession.session;


    /**
     * 일반 세션 목록을 커서 기반으로 조회합니다.
     *
     * <p>커서 조건 (createdAt, id 기준):
     * - createdAt이 더 과거
     * - 같다면 id가 더 작음
     *
     * <p>정렬 기준 (최신순):
     * - createdAt 내림차순
     * - id 내림차순
     */
    @Override
    public SessionListResponse<Session> findAllByCursor(SessionListQueryRequest request) {
        int size = request.size() != null ? request.size() : 10;
        
        // 필터링 조건 구성
        BooleanExpression filterCondition = buildFilterCondition(request);
        
        // 커서 조건 정의
        BooleanExpression cursorCondition = buildCreatedAtCursorCondition(filterCondition, request.cursor(), request.createdAt());
        
        // 쿼리 실행
        List<Session> results = queryFactory
                .selectFrom(session)
                .where(cursorCondition)
                .orderBy(session.createdAt.desc(), session.id.desc())
                .limit(size + 1)
                .fetch();
        
        // 응답 구성
        boolean hasNext = results.size() > size;
        if (hasNext) results.remove(size); // 초과분 제거
        Long nextCursor = hasNext ? results.getLast().getId() : null;
        LocalDateTime nextCreatedAt = hasNext ? results.getLast().getCreatedAt() : null;
        return new SessionListResponse<>(results, nextCursor, nextCreatedAt, hasNext);
    }

    /**
     * 인기 세션 목록을 커서 기반으로 조회합니다. (최근 2주 이내의 조회수 순)
     *
     * <p>커서 조건 (viewCount, createdAt, id 우선순위로 비교):
     * - viewCount가 더 작음
     * - 같다면 createdAt이 더 과거
     * - 같다면 id가 더 작음
     *
     * <p>정렬 기준 (인기순):
     * - viewCount 내림차순
     * - createdAt 내림차순
     * - id 내림차순
     */
    @Override
    public SessionBestListResponse<Session> findAllBestSessionsByCursor(SessionBestListRequest request) {
        int size = request.size() != null ? request.size() : 10;
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        // 커서 조건 정의
        BooleanExpression cursorCondition = null;
        if (request.cursor() != null && request.createdAt() != null && request.viewCount() != null) {
            cursorCondition = session.viewCount.lt(request.viewCount())
                    .or(session.viewCount.eq(request.viewCount())
                            .and(session.createdAt.lt(request.createdAt())
                                    .or(session.createdAt.eq(request.createdAt())
                                            .and(session.id.lt(request.cursor())))));
        }
        // 쿼리 실행
        List<Session> results = queryFactory
                .selectFrom(session)
                .where(
                        session.createdAt.goe(twoWeeksAgo),
                        cursorCondition
                )
                .orderBy(
                        session.viewCount.desc(),
                        session.createdAt.desc(),
                        session.id.desc()
                )
                .limit(size + 1)
                .fetch();
        // 응답 구성
        boolean hasNext = results.size() > size;
        if (hasNext) results.remove(size);
        Long nextCursor = hasNext ? results.getLast().getId() : null;
        LocalDateTime nextCreatedAt = hasNext ? results.getLast().getCreatedAt() : null;
        Integer nextViewCount = hasNext ? results.getLast().getViewCount() : null;
        return new SessionBestListResponse<>(results, nextCursor, nextCreatedAt, nextViewCount, hasNext);
    }

    /**
     * createdAt 기준 커서 페이지네이션 조건을 구성하는 공통 메서드
     */
    private BooleanExpression buildCreatedAtCursorCondition(BooleanExpression baseCondition, Long cursor, LocalDateTime createdAt) {
        BooleanExpression cursorCondition = baseCondition;
        if (cursor != null && createdAt != null) {
            BooleanExpression paginationCondition = session.createdAt.lt(createdAt)
                    .or(session.createdAt.eq(createdAt)
                            .and(session.id.lt(cursor)));
            cursorCondition = cursorCondition != null ? 
                cursorCondition.and(paginationCondition) : paginationCondition;
        }
        return cursorCondition;
    }

    /**
     * 필터링 조건을 구성하는 메서드
     */
    private BooleanExpression buildFilterCondition(SessionListQueryRequest request) {
        BooleanExpression condition = null;
        
        condition = addCondition(condition, buildCategoryCondition(request.category()));
        condition = addCondition(condition, buildPositionCondition(request.position()));
        condition = addCondition(condition, buildDateCondition(request.date()));
        
        return condition;
    }
    
    /**
     * 카테고리 필터링 조건을 구성하는 메서드
     */
    private BooleanExpression buildCategoryCondition(String category) {
        if (category != null && !category.isEmpty()) {
            return session.category.eq(category);
        }
        return null;
    }
    
    /**
     * 포지션 필터링 조건을 구성하는 메서드
     */
    private BooleanExpression buildPositionCondition(List<String> positions) {
        if (positions == null || positions.isEmpty()) {
            return null;
        }
        
        BooleanExpression positionCondition = null;
        for (String pos : positions) {
            if (positionCondition == null) {
                positionCondition = session.position.eq(pos);
            } else {
                positionCondition = positionCondition.or(session.position.eq(pos));
            }
        }
        return positionCondition;
    }
    
    /**
     * 기간 필터링 조건을 구성하는 메서드
     */
    private BooleanExpression buildDateCondition(List<String> dates) {
        if (dates == null || dates.isEmpty()) {
            return null;
        }
        
        BooleanExpression dateCondition = null;
        for (String dateStr : dates) {
            if (dateCondition == null) {
                dateCondition = session.date.eq(dateStr);
            } else {
                dateCondition = dateCondition.or(session.date.eq(dateStr));
            }
        }
        return dateCondition;
    }
    
    /**
     * 조건을 안전하게 추가하는 헬퍼 메서드
     */
    private BooleanExpression addCondition(BooleanExpression existing, BooleanExpression newCondition) {
        if (newCondition == null) return existing;
        return existing == null ? newCondition : existing.and(newCondition);
    }
}
