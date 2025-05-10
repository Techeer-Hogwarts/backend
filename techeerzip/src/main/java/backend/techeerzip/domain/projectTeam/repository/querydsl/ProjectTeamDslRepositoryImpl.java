package backend.techeerzip.domain.projectTeam.repository.querydsl;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.QProjectTeam;
import backend.techeerzip.domain.projectTeam.mapper.ProjectTeamMapper;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;

@Repository
public class ProjectTeamDslRepositoryImpl extends AbstractQuerydslRepository
        implements ProjectTeamDslRepository {

    private static final QProjectTeam PROJECT_TEAM = QProjectTeam.projectTeam;

    public ProjectTeamDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(ProjectTeam.class, em, factory);
    }

    public List<ProjectTeamGetAllResponse> fetchProjectTeams(
            List<PositionNumType> positionNumType, Boolean isRecruited, Boolean isFinished) {
        final BooleanBuilder builder = new BooleanBuilder();

        if (isRecruited != null) {
            builder.and(PROJECT_TEAM.isRecruited.eq(isRecruited));
        }
        if (isFinished != null) {
            builder.and(PROJECT_TEAM.isFinished.eq(isFinished));
        }

        if (positionNumType != null && !positionNumType.isEmpty()) {
            final BooleanBuilder positionFilter = new BooleanBuilder();
            for (PositionNumType p : positionNumType) {
                positionFilter.or(p.getField(PROJECT_TEAM).gt(0));
            }
            builder.and(positionFilter);
        }
        List<ProjectTeam> teams =
                selectFrom(PROJECT_TEAM)
                        .where(builder)
                        .orderBy(PROJECT_TEAM.createdAt.desc())
                        .fetch();
        return teams.stream().map(ProjectTeamMapper::toGetAllResponse).toList();
    }
}
