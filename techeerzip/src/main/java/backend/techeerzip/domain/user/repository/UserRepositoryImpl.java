package backend.techeerzip.domain.user.repository;

import static backend.techeerzip.domain.projectMember.entity.QProjectMember.projectMember;
import static backend.techeerzip.domain.projectTeam.entity.QProjectTeam.projectTeam;
import static backend.techeerzip.domain.studyMember.entity.QStudyMember.studyMember;
import static backend.techeerzip.domain.studyTeam.entity.QStudyTeam.studyTeam;
import static backend.techeerzip.domain.user.entity.QUser.user;
import static backend.techeerzip.domain.userExperience.entity.QUserExperience.userExperience;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.global.exception.CursorException;

@Repository
public class UserRepositoryImpl extends QuerydslRepositorySupport implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager entityManager) {
        super(User.class);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<User> findUsersWithCursor(
            Long cursorId,
            List<String> positions,
            List<Integer> years,
            List<String> universities,
            List<String> grades,
            int limit,
            String sortBy) {

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(user.isDeleted.eq(false));

        if (positions != null && !positions.isEmpty()) {
            whereClause.and(user.mainPosition.in(positions));
        }

        if (years != null && !years.isEmpty()) {
            whereClause.and(user.year.in(years));
        }

        if (universities != null && !universities.isEmpty()) {
            whereClause.and(user.school.in(universities));
        }

        if (grades != null && !grades.isEmpty()) {
            whereClause.and(user.grade.in(grades));
        }

        if (cursorId != null) {
            User cursorUser = queryFactory.selectFrom(user).where(user.id.eq(cursorId)).fetchOne();
            if (cursorUser == null) {
                throw new CursorException();
            }
            whereClause.and(getCursorCondition(sortBy, cursorUser));
        }

        return queryFactory
                .selectFrom(user)
                .where(whereClause)
                .orderBy(getSortOrder(sortBy))
                .limit(limit + 1)
                .fetch();
    }

    private BooleanBuilder getCursorCondition(String sortBy, User cursorUser) {
        BooleanBuilder builder = new BooleanBuilder();

        if ("name".equals(sortBy)) {
            builder.and(user.name.gt(cursorUser.getName()));
        } else if ("year".equals(sortBy)) {
            builder.and(
                    user.year
                            .gt(cursorUser.getYear())
                            .or(
                                    user.year
                                            .eq(cursorUser.getYear())
                                            .and(user.name.gt(cursorUser.getName()))));
        } else {
            // 기본 값: 기수 순
            builder.and(
                    user.year
                            .gt(cursorUser.getYear())
                            .or(
                                    user.year
                                            .eq(cursorUser.getYear())
                                            .and(user.name.gt(cursorUser.getName()))));
        }
        return builder;
    }

    private OrderSpecifier<?>[] getSortOrder(String sortBy) {
        if ("name".equals(sortBy)) {
            return new OrderSpecifier[] { user.name.asc() };
        } else if ("year".equals(sortBy)) {
            return new OrderSpecifier[] { user.year.asc(), user.name.asc() };
        }
        return new OrderSpecifier[] { user.year.asc(), user.name.asc() };
    }

    @Override
    public Optional<User> findByIdWithNonDeletedRelations(Long userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .leftJoin(user.projectMembers, projectMember)
                        .leftJoin(projectMember.projectTeam, projectTeam)
                        .leftJoin(user.studyMembers, studyMember)
                        .leftJoin(studyMember.studyTeam, studyTeam)
                        .leftJoin(user.experiences, userExperience)
                        .where(
                                user.id.eq(userId),
                                user.isDeleted.eq(false),
                                // 프로젝트 팀 관련 조건
                                projectTeam.isDeleted.eq(false).or(projectTeam.isNull()),
                                projectMember.isDeleted.eq(false).or(projectMember.isNull()),
                                // 스터디 팀 관련 조건
                                studyTeam.isDeleted.eq(false).or(studyTeam.isNull()),
                                studyMember.isDeleted.eq(false).or(studyMember.isNull()),
                                // 경력 관련 조건
                                userExperience.isDeleted.eq(false).or(userExperience.isNull()))
                        .fetchOne());
    }
}
