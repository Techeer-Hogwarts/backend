package backend.techeerzip.domain.like.repository;

import static backend.techeerzip.domain.blog.entity.QBlog.blog;
import static backend.techeerzip.domain.like.entity.QLike.like;
import static backend.techeerzip.domain.resume.entity.QResume.resume;
import static backend.techeerzip.domain.session.entity.QSession.session;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.like.entity.Like;

@Repository
public class LikeRepositoryImpl extends QuerydslRepositorySupport implements LikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public LikeRepositoryImpl(EntityManager entityManager) {
        super(Like.class);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Like> findLikesWithCursor(Long userId, String category, Long cursorId, int limit) {
        return queryFactory
                .selectFrom(like)
                .leftJoin(blog)
                .on(like.category.eq("BLOG").and(like.contentId.eq(blog.id)))
                .leftJoin(session)
                .on(like.category.eq("SESSION").and(like.contentId.eq(session.id)))
                .leftJoin(resume)
                .on(like.category.eq("RESUME").and(like.contentId.eq(resume.id)))
                .where(
                        like.user.id.eq(userId),
                        like.category.eq(category),
                        like.isDeleted.eq(false),
                        cursorId != null ? like.contentId.lt(cursorId) : null)
                .orderBy(like.contentId.desc())
                .limit(limit + 1)
                .fetch();
    }
}
