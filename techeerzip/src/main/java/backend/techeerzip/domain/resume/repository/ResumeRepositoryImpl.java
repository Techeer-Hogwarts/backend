package backend.techeerzip.domain.resume.repository;

import static backend.techeerzip.domain.resume.entity.QResume.resume;
import static backend.techeerzip.domain.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

import backend.techeerzip.domain.resume.entity.Resume;

@Repository
public class ResumeRepositoryImpl implements ResumeRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public ResumeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Resume> findResumesWithCursor(
            List<String> position,
            List<Integer> year,
            String category,
            Long cursorId,
            Integer limit
    ) {
        Resume cursorResume = (cursorId != null)
                ? queryFactory.selectFrom(resume).where(resume.id.eq(cursorId)).fetchOne()
                : null;

        return queryFactory
                .selectFrom(resume)
                .join(resume.user, user).fetchJoin()
                .where(
                        resume.isDeleted.eq(false),
                        positionIn(position),
                        yearIn(year),
                        categoryEq(category),
                        cursorResume != null ? resume.title.gt(cursorResume.getTitle()) : null
                )
                .orderBy(resume.createdAt.desc())
                .limit((limit != null && limit > 0) ? limit + 1 : 11)
                .fetch();
    }

    @Override
    public List<Resume> findBestResumes(LocalDateTime createdAt) {
        return queryFactory
                .selectFrom(resume)
                .join(resume.user, user).fetchJoin()
                .where(
                        resume.isDeleted.eq(false),
                        resume.createdAt.goe(createdAt)
                )
                .fetch();
    }

    private BooleanExpression positionIn(List<String> position) {
        return (position != null && !position.isEmpty()) ? resume.position.in(position) : null;
    }

    private BooleanExpression yearIn(List<Integer> year) {
        return (year != null && !year.isEmpty()) ? resume.user.year.in(year) : null;
    }

    private BooleanExpression categoryEq(String category) {
        return (category != null && !category.isBlank()) ? resume.category.eq(category) : null;
    }
}
