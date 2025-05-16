package backend.techeerzip.domain.projectTeam.repository.querydsl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamQueryRequest.GetTeamQuery;
import backend.techeerzip.domain.projectTeam.dto.response.SliceNextInfo;
import backend.techeerzip.domain.projectTeam.dto.response.TeamUnionInfo;
import backend.techeerzip.domain.projectTeam.dto.response.TeamUnionSliceYoungInfo;
import backend.techeerzip.domain.projectTeam.entity.QTeamUnionView;
import backend.techeerzip.domain.projectTeam.entity.TeamUnionView;
import backend.techeerzip.domain.projectTeam.type.TeamType;

@Repository
public class TeamUnionViewDslRepositoryImpl extends AbstractQuerydslRepository
        implements TeamUnionViewDslRepository {

    private static final QTeamUnionView TU = QTeamUnionView.teamUnionView;

    protected TeamUnionViewDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(TeamUnionView.class, em, factory);
    }

    private static BooleanExpression getCreateAtCondition(
            UUID viewId, LocalDateTime createdAtCursor) {
        return TU.isDeleted
                .isFalse()
                .and(
                        TU.createdAt
                                .lt(createdAtCursor)
                                .or(TU.createdAt.eq(createdAtCursor).and(TU.globalId.lt(viewId))));
    }

    private static TeamUnionSliceYoungInfo mapToYoungTeam(
            List<TeamUnionInfo> teams, SliceNextInfo last) {
        List<Long> projects = new ArrayList<>();
        List<Long> studies = new ArrayList<>();

        for (TeamUnionInfo i : teams) {
            if (i.getTeamType().equals(TeamType.PROJECT)) {
                projects.add(i.getId());
            }
            if (i.getTeamType().equals(TeamType.STUDY)) {
                studies.add(i.getId());
            }
        }

        return new TeamUnionSliceYoungInfo(projects, studies, last);
    }

    private static SliceNextInfo setNextInfo(List<TeamUnionInfo> sortedTeams, Long limit) {
        if (sortedTeams.size() > limit) {
            final TeamUnionInfo last = sortedTeams.removeLast();
            return SliceNextInfo.builder()
                    .hasNext(true)
                    .globalId(last.getGlobalId())
                    .createdAt(last.getCreatedAt())
                    .build();
        }
        return SliceNextInfo.builder().hasNext(false).build();
    }

    private static List<TeamUnionInfo> ensureMaxSize(List<TeamUnionInfo> unionTeams, Long limit) {
        if (unionTeams.size() > limit) {
            return unionTeams.subList(0, limit.intValue());
        }
        return unionTeams;
    }

    public TeamUnionSliceYoungInfo fetchSliceBeforeCreatedAtDescCursor(GetTeamQuery request) {
        final UUID globalId = request.getGlobalId();
        final LocalDateTime createdAtCursor = request.getCreateAtCursor();
        final Long limit = request.getLimit();
        final List<TeamType> teamTypes = request.getTeamTypes();
        final Boolean isRecruited = request.getIsRecruited();
        final Boolean isFinished = request.getIsFinished();

        final BooleanExpression condition = getCreateAtCondition(globalId, createdAtCursor);
        if (isFinished != null) {
            condition.and(TU.isFinished.eq(isFinished));
        }
        if (isRecruited != null) {
            condition.and(TU.isRecruited.eq(isRecruited));
        }
        if (teamTypes.size() == 1) {
            condition.and(TU.teamType.eq(teamTypes.getFirst()));
        }
        final List<TeamUnionInfo> teams =
                select(Projections.fields(TeamUnionInfo.class, TU.globalId, TU.id, TU.teamType))
                        .where(condition)
                        .orderBy(TU.createdAt.desc())
                        .limit(limit + 1)
                        .fetch();
        final SliceNextInfo lastInfo = setNextInfo(teams, limit);
        final List<TeamUnionInfo> fitTeams = ensureMaxSize(teams, limit);

        return mapToYoungTeam(fitTeams, lastInfo);
    }
}
