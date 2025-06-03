package backend.techeerzip.domain.bookmark.repository;

import static backend.techeerzip.domain.blog.entity.QBlog.blog;
import static backend.techeerzip.domain.bookmark.entity.QBookmark.bookmark;
import static backend.techeerzip.domain.resume.entity.QResume.resume;
import static backend.techeerzip.domain.session.entity.QSession.session;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.bookmark.entity.Bookmark;

@Repository
public class BookmarkRepositoryImpl extends QuerydslRepositorySupport
        implements BookmarkRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public BookmarkRepositoryImpl(EntityManager entityManager) {
        super(Bookmark.class);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Bookmark> findBookmarksWithCursor(
            Long userId, String category, Long cursorId, int limit) {
        BooleanBuilder whereBuilder =
                new BooleanBuilder()
                        .and(bookmark.user.id.eq(userId))
                        .and(bookmark.category.eq(category))
                        .and(bookmark.isDeleted.eq(false));

        if (cursorId != null) {
            whereBuilder.and(bookmark.contentId.lt(cursorId));
        }

        JPAQuery<Bookmark> query =
                queryFactory
                        .selectFrom(bookmark)
                        .where(whereBuilder)
                        .orderBy(bookmark.contentId.desc())
                        .limit(limit + 1);

        switch (category) {
            case "BLOG" -> query.leftJoin(blog).on(bookmark.contentId.eq(blog.id));
            case "SESSION" -> query.leftJoin(session).on(bookmark.contentId.eq(session.id));
            case "RESUME" -> query.leftJoin(resume).on(bookmark.contentId.eq(resume.id));
        }

        return query.fetch();
    }
}
