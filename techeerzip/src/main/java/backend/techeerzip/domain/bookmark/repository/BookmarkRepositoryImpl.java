package backend.techeerzip.domain.bookmark.repository;

import static backend.techeerzip.domain.blog.entity.QBlog.blog;
import static backend.techeerzip.domain.bookmark.entity.QBookmark.bookmark;
import static backend.techeerzip.domain.resume.entity.QResume.resume;
import static backend.techeerzip.domain.session.entity.QSession.session;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

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
        return queryFactory
                .selectFrom(bookmark)
                .leftJoin(blog)
                .on(bookmark.category.eq("BLOG").and(bookmark.contentId.eq(blog.id)))
                .leftJoin(session)
                .on(bookmark.category.eq("SESSION").and(bookmark.contentId.eq(session.id)))
                .leftJoin(resume)
                .on(bookmark.category.eq("RESUME").and(bookmark.contentId.eq(resume.id)))
                .where(
                        bookmark.user.id.eq(userId),
                        bookmark.category.eq(category),
                        bookmark.isDeleted.eq(false),
                        cursorId != null ? bookmark.contentId.lt(cursorId) : null)
                .orderBy(bookmark.contentId.desc())
                .limit(limit + 1)
                .fetch();
    }
}
