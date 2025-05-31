package backend.techeerzip.domain.event.repository;

import static backend.techeerzip.domain.event.entity.QEvent.event;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.event.entity.Event;

@Repository
public class EventRepositoryImpl extends QuerydslRepositorySupport
        implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public EventRepositoryImpl(EntityManager entityManager) {
        super(Event.class);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Event> findEventsWithCursor(
            Long cursorId, String keyword, List<String> categories, int limit) {

        Event cursorEvent =
                cursorId != null && cursorId > 0
                        ? queryFactory.selectFrom(event).where(event.id.eq(cursorId)).fetchOne()
                        : null;

        return queryFactory
                .selectFrom(event)
                .where(
                        event.isDeleted.eq(false),
                        keywordEq(keyword),
                        categoryIn(categories),
                        cursorEvent != null ? getCursorCondition(cursorEvent) : null)
                .orderBy(getOrderSpecifiers())
                .limit(limit + 1)
                .fetch();
    }

    // --- private 헬퍼 메서드 ---
    private BooleanExpression keywordEq(String keyword) {
        return keyword != null && !keyword.trim().isEmpty()
                ? event.title.lower().like("%" + keyword.toLowerCase() + "%")
                : null;
    }

    private BooleanExpression categoryIn(List<String> categories) {
        return categories != null && !categories.isEmpty() ? event.category.in(categories) : null;
    }

    private BooleanExpression getCursorCondition(Event cursorEvent) {
        return event.createdAt
                .lt(cursorEvent.getCreatedAt())
                .or(
                        event.createdAt
                                .eq(cursorEvent.getCreatedAt())
                                .and(event.id.lt(cursorEvent.getId())));
    }

    private OrderSpecifier<?>[] getOrderSpecifiers() {
        // 생성일이 같으면 ID 내림차순으로 고정
        return new OrderSpecifier<?>[] {event.createdAt.desc(), event.id.desc()};
    }
}
