package backend.techeerzip.domain.blog.repository;

import static backend.techeerzip.domain.blog.entity.QBlog.blog;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.blog.entity.Blog;

@Repository
public class BlogRepositoryImpl extends QuerydslRepositorySupport implements BlogRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BlogRepositoryImpl(EntityManager entityManager) {
        super(Blog.class);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Blog> findBlogsWithCursor(
            Long cursorId, String category, String sortBy, int limit) {
        Blog cursorBlog = cursorId != null
                ? queryFactory.selectFrom(blog).where(blog.id.eq(cursorId)).fetchOne()
                : null;

        return queryFactory
                .selectFrom(blog)
                .where(
                        blog.isDeleted.eq(false),
                        category != null ? blog.category.eq(category) : null,
                        cursorBlog != null ? getCursorCondition(cursorBlog, sortBy) : null)
                .orderBy(getOrderSpecifiers(sortBy))
                .limit(limit + 1)
                .fetch();
    }

    @Override
    public List<Blog> findPopularBlogsWithCursor(Long cursorId, int limit) {
        Blog cursorBlog = cursorId != null
                ? queryFactory.selectFrom(blog).where(blog.id.eq(cursorId)).fetchOne()
                : null;

        // 2주 전 날짜 계산
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);

        return queryFactory
                .selectFrom(blog)
                .where(
                        blog.isDeleted.eq(false),
                        blog.date.after(twoWeeksAgo), // 2주 이내의 글만
                        cursorBlog != null
                                ? blog.viewCount
                                        .add(blog.likeCount.multiply(10))
                                        .lt(
                                                cursorBlog.getViewCount()
                                                        + (cursorBlog.getLikeCount() * 10))
                                : null)
                .orderBy(
                        blog.viewCount.add(blog.likeCount.multiply(10)).desc(),
                        blog.date.desc() // 인기도가 같을 경우 최신순으로 정렬
                )
                .limit(limit + 1)
                .fetch();
    }

    private BooleanExpression getCursorCondition(Blog cursorBlog, String sortBy) {
        return switch (sortBy) {
            case "viewCount" -> blog.viewCount
                    .lt(cursorBlog.getViewCount())
                    .or(
                            blog.viewCount
                                    .eq(cursorBlog.getViewCount())
                                    .and(blog.date.lt(cursorBlog.getDate())))
                    .or(
                            blog.viewCount
                                    .eq(cursorBlog.getViewCount())
                                    .and(blog.date.eq(cursorBlog.getDate()))
                                    .and(blog.id.lt(cursorBlog.getId())));
            case "name" -> blog.title
                    .gt(cursorBlog.getTitle())
                    .or(
                            blog.title
                                    .eq(cursorBlog.getTitle())
                                    .and(blog.date.lt(cursorBlog.getDate())))
                    .or(
                            blog.title
                                    .eq(cursorBlog.getTitle())
                                    .and(blog.date.eq(cursorBlog.getDate()))
                                    .and(blog.id.lt(cursorBlog.getId())));
            default -> blog.date
                    .lt(cursorBlog.getDate())
                    .or(
                            blog.date
                                    .eq(cursorBlog.getDate())
                                    .and(blog.id.lt(cursorBlog.getId())));
        };
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(String sortBy) {
        return switch (sortBy) {
            case "viewCount" -> new OrderSpecifier<?>[] {
                    blog.viewCount.desc(),
                    blog.date.desc(),
                    blog.id.desc()
            };
            case "name" -> new OrderSpecifier<?>[] {
                    blog.title.asc(),
                    blog.date.desc(),
                    blog.id.desc()
            };
            default -> new OrderSpecifier<?>[] {
                    blog.date.desc(),
                    blog.id.desc()
            };
        };
    }

    public List<Blog> findBlogsForChallenge(
            List<Long> blogIds, Blog cursorBlog, int limit, String sortBy) {
        return queryFactory
                .selectFrom(blog)
                .where(blog.id.in(blogIds), cursorCondition(cursorBlog, sortBy))
                .orderBy(orderSpecifier(sortBy))
                .limit(limit)
                .fetch();
    }

    private BooleanExpression cursorCondition(Blog cursorBlog, String sortBy) {
        if (cursorBlog == null) {
            return null;
        }

        switch (sortBy) {
            case "latest":
                return blog.date.lt(cursorBlog.getDate());
            case "viewCount":
                return blog.viewCount.lt(cursorBlog.getViewCount());
            case "name":
                return blog.title.lt(cursorBlog.getTitle());
            default:
                return blog.date.lt(cursorBlog.getDate());
        }
    }

    private OrderSpecifier<?> orderSpecifier(String sortBy) {
        switch (sortBy) {
            case "latest":
                return blog.date.desc();
            case "viewCount":
                return blog.viewCount.desc();
            case "name":
                return blog.title.asc();
            default:
                return blog.date.desc();
        }
    }
}
