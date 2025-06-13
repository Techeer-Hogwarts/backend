package backend.techeerzip.domain.user.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import backend.techeerzip.domain.studyMember.entity.StudyMember;
import backend.techeerzip.domain.user.entity.User;
import backend.techeerzip.domain.userExperience.entity.UserExperience;
import backend.techeerzip.global.exception.CursorException;

@Repository
public class UserRepositoryImpl extends QuerydslRepositorySupport implements UserRepositoryCustom {

    private final EntityManager entityManager;

    public UserRepositoryImpl(EntityManager entityManager) {
        super(User.class);
        this.entityManager = entityManager;
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

        StringBuilder jpql = new StringBuilder("SELECT u FROM User u WHERE u.isDeleted = false");

        if (positions != null && !positions.isEmpty()) {
            jpql.append(" AND u.mainPosition IN :positions");
        }
        if (years != null && !years.isEmpty()) {
            jpql.append(" AND u.year IN :years");
        }
        if (universities != null && !universities.isEmpty()) {
            jpql.append(" AND u.school IN :universities");
        }
        if (grades != null && !grades.isEmpty()) {
            jpql.append(" AND u.grade IN :grades");
        }

        if (cursorId != null) {
            User cursorUser = entityManager.find(User.class, cursorId);
            if (cursorUser == null) {
                throw new CursorException();
            }
            jpql.append(getCursorCondition(sortBy));
        }

        jpql.append(getSortOrder(sortBy));

        TypedQuery<User> query = entityManager.createQuery(jpql.toString(), User.class);

        if (positions != null && !positions.isEmpty()) {
            query.setParameter("positions", positions);
        }
        if (years != null && !years.isEmpty()) {
            query.setParameter("years", years);
        }
        if (universities != null && !universities.isEmpty()) {
            query.setParameter("universities", universities);
        }
        if (grades != null && !grades.isEmpty()) {
            query.setParameter("grades", grades);
        }
        if (cursorId != null) {
            User cursorUser = entityManager.find(User.class, cursorId);
            if ("name".equals(sortBy)) {
                query.setParameter("cursorName", cursorUser.getName());
            } else {
                query.setParameter("cursorYear", cursorUser.getYear());
                query.setParameter("cursorName", cursorUser.getName());
            }
        }

        query.setMaxResults(limit + 1);
        return query.getResultList();
    }

    private String getCursorCondition(String sortBy) {
        if ("name".equals(sortBy)) {
            return " AND u.name > :cursorName";
        } else {
            return " AND (u.year > :cursorYear OR (u.year = :cursorYear AND u.name > :cursorName))";
        }
    }

    private String getSortOrder(String sortBy) {
        if ("name".equals(sortBy)) {
            return " ORDER BY u.name ASC";
        } else {
            return " ORDER BY u.year ASC, u.name ASC";
        }
    }

    @Override
    public Optional<User> findByIdWithNonDeletedRelations(Long userId) {
        try {
            // 기본 유저 정보 조회
            String userJpql =
                    """
                    SELECT DISTINCT u FROM User u
                    LEFT JOIN FETCH u.projectMembers pm
                    LEFT JOIN FETCH pm.projectTeam pt
                    WHERE u.id = :userId
                    AND u.isDeleted = false
                    AND (pm IS NULL OR pm.isDeleted = false)
                    AND (pt IS NULL OR pt.isDeleted = false)
                    """;

            User user =
                    entityManager
                            .createQuery(userJpql, User.class)
                            .setParameter("userId", userId)
                            .getSingleResult();

            // 스터디 멤버 정보 조회
            String studyJpql =
                    """
                    SELECT sm FROM StudyMember sm
                    LEFT JOIN FETCH sm.studyTeam st
                    WHERE sm.user.id = :userId
                    AND sm.isDeleted = false
                    AND (st IS NULL OR st.isDeleted = false)
                    """;

            TypedQuery<StudyMember> studyQuery =
                    entityManager.createQuery(studyJpql, StudyMember.class);
            studyQuery.setParameter("userId", userId);
            List<StudyMember> studyMembers = studyQuery.getResultList();

            // 경력 정보 조회
            String experienceJpql =
                    """
                    SELECT e FROM UserExperience e
                    WHERE e.userId = :userId
                    AND e.isDeleted = false
                    """;

            TypedQuery<UserExperience> experienceQuery =
                    entityManager.createQuery(experienceJpql, UserExperience.class);
            experienceQuery.setParameter("userId", userId);
            List<UserExperience> experiences = experienceQuery.getResultList();

            // 결과 설정
            user.getStudyMembers().clear();
            user.getStudyMembers().addAll(studyMembers);

            user.getExperiences().clear();
            user.getExperiences().addAll(experiences);

            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
