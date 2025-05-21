package backend.techeerzip.domain.session.repository;

import backend.techeerzip.domain.session.entity.QSession;
import backend.techeerzip.domain.session.entity.Session;
import backend.techeerzip.global.common.CursorPageViewCountRequest;
import backend.techeerzip.global.common.CursorPageViewCountResponse;
import backend.techeerzip.global.common.CursorPageCreatedAtRequest;
import backend.techeerzip.global.common.CursorPageCreatedAtResponse;
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
    public CursorPageCreatedAtResponse<Session> findAllByCursor(CursorPageCreatedAtRequest request) {
        int size = request.size() != null ? request.size() : 10;
        // 커서 조건 정의
        BooleanExpression cursorCondition = null;
        if (request.cursor() != null && request.createdAt() != null) {
            cursorCondition = session.createdAt.lt(request.createdAt())
                    .or(session.createdAt.eq(request.createdAt())
                            .and(session.id.lt(request.cursor())));
        }
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
        return new CursorPageCreatedAtResponse<>(results, nextCursor, nextCreatedAt, hasNext);
    }

    /**
     * 특정 유저가 작성한 세션 목록을 커서 기반으로 조회합니다.
     *
     * <p>기본 조건:
     * - userId와 일치하는 유저의 세션만 조회
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
    public CursorPageCreatedAtResponse<Session> findAllByUserIdCursor(Long userId, CursorPageCreatedAtRequest request) {
        int size = request.size() != null ? request.size() : 10;
        // 유저 조건과 결합한 커서 조건 정의
        BooleanExpression cursorCondition = session.user.id.eq(userId);
        if (request.cursor() != null && request.createdAt() != null) {
            BooleanExpression paginationCondition = session.createdAt.lt(request.createdAt())
                    .or(session.createdAt.eq(request.createdAt())
                            .and(session.id.lt(request.cursor())));
            cursorCondition = cursorCondition.and(paginationCondition);
        }
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
        return new CursorPageCreatedAtResponse<>(results, nextCursor, nextCreatedAt, hasNext);
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
    public CursorPageViewCountResponse<Session> findAllBestSessionsByCursor(CursorPageViewCountRequest request) {
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
        return new CursorPageViewCountResponse<>(results, nextCursor, nextCreatedAt, nextViewCount, hasNext);
    }
}
