package backend.techeerzip.domain.techBloggingChallenge.repository;

import static backend.techeerzip.domain.blog.entity.QBlog.blog;
import static backend.techeerzip.domain.techBloggingChallenge.entity.QTechBloggingAttendance.techBloggingAttendance;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.blog.entity.Blog;

@Repository
public class TechBloggingAttendanceRepositoryImpl
        implements TechBloggingAttendanceRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public TechBloggingAttendanceRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Blog> findBlogsByRoundWithCursor(
            Long roundId, Long termId, String sort, Blog cursorBlog, int size) {
        return queryFactory
                .select(blog)
                .from(techBloggingAttendance)
                .join(techBloggingAttendance.blog, blog)
                .where(
                        roundIdCondition(roundId),
                        techBloggingAttendance.isDeleted.eq(false),
                        blog.isDeleted.eq(false),
                        termId != null
                                ? techBloggingAttendance.techBloggingRound.term.id.eq(termId)
                                : null,
                        cursorCondition(cursorBlog, sort))
                .orderBy(orderSpecifier(sort))
                .limit(size + 1)
                .fetch();
    }

    @Override
    public List<Long> getValidBlogIds(Long termId, Long roundId) {
        return queryFactory
                .select(techBloggingAttendance.blog.id)
                .from(techBloggingAttendance)
                .where(
                        roundIdCondition(roundId),
                        techBloggingAttendance.isDeleted.eq(false),
                        termId != null
                                ? techBloggingAttendance.techBloggingRound.term.id.eq(termId)
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
                blog.viewCount.desc(), blog.createdAt.desc() // 조회수가 같을 경우 최신순으로 정렬
            };
        } else if ("name".equals(sort)) {
            return new OrderSpecifier<?>[] {
                blog.title.asc(), blog.createdAt.desc() // 제목이 같을 경우 최신순으로 정렬
            };
        } else {
            return new OrderSpecifier<?>[] {blog.createdAt.desc()};
        }
    }
}
