package backend.techeerzip.domain.projectTeam.repository.querydsl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.common.util.DslBooleanBuilder;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQuery;
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

    private static TeamUnionSliceYoungInfo mapToYoungTeam(
            List<TeamUnionInfo> teams, SliceNextInfo last) {
        final List<Long> projects = new ArrayList<>();
        final List<Long> studies = new ArrayList<>();

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

    public TeamUnionSliceYoungInfo fetchSliceBeforeCreatedAtDescCursor(GetTeamsQuery request) {
        final BooleanExpression condition = buildSearchCondition(request);
        final List<TeamUnionInfo> teams = selectTeamUnionInfos(condition, request.getLimit());
        final SliceNextInfo lastInfo = setNextInfo(teams, request.getLimit());
        final List<TeamUnionInfo> fitTeams = ensureMaxSize(teams, request.getLimit());

        return mapToYoungTeam(fitTeams, lastInfo);
    }

    private BooleanExpression buildSearchCondition(GetTeamsQuery request) {
        final BooleanExpression teamTypeExpr =
                request.getTeamTypes().size() == 1
                        ? TU.teamType.eq(request.getTeamTypes().getFirst())
                        : null;
        return DslBooleanBuilder.builder()
                .andIfNotNull(
                        buildCursorCondition(request.getGlobalId(), request.getCreateAtCursor()))
                .andIfNotNull(request.getIsRecruited(), TU.isRecruited::eq)
                .andIfNotNull(request.getIsFinished(), TU.isFinished::eq)
                .andIfNotNull(teamTypeExpr)
                .andIfNotNull(TU.isDeleted.eq(false))
                .build();
    }

    private BooleanExpression buildCursorCondition(UUID globalId, LocalDateTime createdAt) {
        if (createdAt == null) return Expressions.TRUE;

        if (globalId != null) {
            return TU.createdAt
                    .lt(createdAt)
                    .or(TU.createdAt.eq(createdAt).and(TU.globalId.lt(globalId)));
        }
        return TU.createdAt.lt(createdAt);
    }

    private List<TeamUnionInfo> selectTeamUnionInfos(BooleanExpression condition, Long limit) {
        return select(
                        Projections.fields(
                                TeamUnionInfo.class, TU.globalId, TU.id, TU.teamType, TU.createdAt))
                .from(TU)
                .where(condition)
                .orderBy(TU.createdAt.desc())
                .limit(limit + 1)
                .fetch();
    }

    private <T> BooleanExpression equalIfNotNull(T value, Function<T, BooleanExpression> fn) {
        return value != null ? fn.apply(value) : null;
    }
}
