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
        Blog cursorBlog =
                cursorId != null
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
        Blog cursorBlog =
                cursorId != null
                        ? queryFactory.selectFrom(blog).where(blog.id.eq(cursorId)).fetchOne()
                        : null;

        // 2주 전 날짜 계산
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);

        return queryFactory
                .selectFrom(blog)
                .where(
                        blog.isDeleted.eq(false),
                        blog.createdAt.after(twoWeeksAgo), // 2주 이내의 글만
                        cursorBlog != null
                                ? blog.viewCount
                                        .add(blog.likeCount.multiply(10))
                                        .lt(
                                                cursorBlog.getViewCount()
                                                        + (cursorBlog.getLikeCount() * 10))
                                : null)
                .orderBy(
                        blog.viewCount.add(blog.likeCount.multiply(10)).desc(),
                        blog.createdAt.desc() // 인기도가 같을 경우 최신순으로 정렬
                        )
                .limit(limit + 1)
                .fetch();
    }

    private BooleanExpression getCursorCondition(Blog cursorBlog, String sortBy) {
        return switch (sortBy) {
            case "viewCount" -> blog.viewCount.lt(cursorBlog.getViewCount());
            case "name" -> blog.title.gt(cursorBlog.getTitle());
            default -> blog.createdAt.lt(cursorBlog.getCreatedAt());
        };
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(String sortBy) {
        return switch (sortBy) {
            case "viewCount" ->
                    new OrderSpecifier<?>[] {
                        blog.viewCount.desc(), blog.createdAt.desc() // 조회수가 같을 경우 최신순으로 정렬
                    };
            case "name" -> new OrderSpecifier<?>[] {blog.title.asc()};
            default -> new OrderSpecifier<?>[] {blog.createdAt.desc()};
        };
    }
}
