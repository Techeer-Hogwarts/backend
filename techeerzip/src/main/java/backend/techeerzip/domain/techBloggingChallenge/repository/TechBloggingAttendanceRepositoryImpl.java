package backend.techeerzip.domain.techBloggingChallenge.repository;

import static backend.techeerzip.domain.blog.entity.QBlog.blog;
import static backend.techeerzip.domain.techBloggingChallenge.entity.QTechBloggingAttendance.techBloggingAttendance;
import static backend.techeerzip.domain.techBloggingChallenge.entity.QTechBloggingRound.techBloggingRound;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.blog.entity.Blog;
import backend.techeerzip.domain.techBloggingChallenge.entity.TechBloggingRound;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TechBloggingAttendanceRepositoryImpl
        implements TechBloggingAttendanceRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final TechBloggingRoundRepository roundRepository;

    @Override
    public List<Blog> findBlogsByRoundWithCursor(
            Long roundId, Long termId, String sort, Blog cursorBlog, int size) {
        return queryFactory
                .select(blog)
                .from(techBloggingAttendance)
                .join(techBloggingAttendance.techBloggingRound, techBloggingRound)
                .join(blog)
                .on(blog.id.eq(techBloggingAttendance.blog.id))
                .where(
                        roundIdCondition(roundId),
                        techBloggingAttendance.isDeleted.eq(false),
                        blog.isDeleted.eq(false),
                        termId != null ? techBloggingRound.term.id.eq(termId) : null,
                        cursorCondition(cursorBlog, sort))
                .orderBy(orderSpecifier(sort))
                .limit(size + 1)
                .fetch();
    }

    @Override
    public List<Long> getValidBlogIds(Long termId, Long roundId) {
        TechBloggingRound round =
                roundId != null
                        ? roundRepository
                                .findById(roundId)
                                .orElseThrow(
                                        () -> new IllegalArgumentException("존재하지 않는 roundId입니다."))
                        : null;

        return queryFactory
                .select(techBloggingAttendance.blog.id)
                .from(techBloggingAttendance)
                .join(techBloggingAttendance.techBloggingRound, techBloggingRound)
                .where(
                        techBloggingAttendance.isDeleted.eq(false),
                        techBloggingRound.term.id.eq(termId),
                        round != null
                                ? techBloggingAttendance.createdAt.between(
                                        round.getStartDate().atStartOfDay(),
                                        round.getEndDate().atTime(23, 59, 59))
                                : null)
                .fetch();
    }

    private BooleanExpression roundIdCondition(Long roundId) {
        return roundId != null ? techBloggingAttendance.techBloggingRound.id.eq(roundId) : null;
    }

    private BooleanExpression cursorCondition(Blog cursorBlog, String sort) {
        if (cursorBlog == null) return null;
        if ("viewCount".equals(sort)) {
            return blog.viewCount
                    .lt(cursorBlog.getViewCount())
                    .or(
                            blog.viewCount
                                    .eq(cursorBlog.getViewCount())
                                    .and(blog.createdAt.lt(cursorBlog.getCreatedAt())))
                    .or(
                            blog.viewCount
                                    .eq(cursorBlog.getViewCount())
                                    .and(blog.createdAt.eq(cursorBlog.getCreatedAt()))
                                    .and(blog.id.lt(cursorBlog.getId())));
        } else if ("name".equals(sort)) {
            return blog.title
                    .gt(cursorBlog.getTitle())
                    .or(
                            blog.title
                                    .eq(cursorBlog.getTitle())
                                    .and(blog.createdAt.lt(cursorBlog.getCreatedAt())))
                    .or(
                            blog.title
                                    .eq(cursorBlog.getTitle())
                                    .and(blog.createdAt.eq(cursorBlog.getCreatedAt()))
                                    .and(blog.id.lt(cursorBlog.getId())));
        } else { // latest
            return blog.createdAt
                    .lt(cursorBlog.getCreatedAt())
                    .or(
                            blog.createdAt
                                    .eq(cursorBlog.getCreatedAt())
                                    .and(blog.id.lt(cursorBlog.getId())));
        }
    }

    private OrderSpecifier<?>[] orderSpecifier(String sort) {
        if ("viewCount".equals(sort)) {
            return new OrderSpecifier<?>[] {
                blog.viewCount.desc(), blog.createdAt.desc(), blog.id.desc() // 조회수가 같을 경우 최신순으로
                // 정렬
            };
        } else if ("name".equals(sort)) {
            return new OrderSpecifier<?>[] {
                blog.title.asc(), blog.createdAt.desc(), blog.id.desc() // 제목이 같을 경우 최신순으로 정렬
            };
        } else {
            return new OrderSpecifier<?>[] {blog.createdAt.desc(), blog.id.desc()};
        }
    }
}
