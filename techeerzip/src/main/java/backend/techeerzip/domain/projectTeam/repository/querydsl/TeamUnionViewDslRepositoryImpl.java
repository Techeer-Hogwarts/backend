package backend.techeerzip.domain.projectTeam.repository.querydsl;

import backend.techeerzip.domain.projectTeam.type.SortType;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
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

    public TeamUnionSliceYoungInfo fetchSliceBeforeCreatedAtDescCursor(GetTeamsQuery request) {
        final BooleanExpression condition = buildCursorCondition(
                request.getGlobalId(),
                request.getDateCursor(),
                request.getCountCursor(),
                request.getSortType()
        );

        final List<TeamUnionInfo> teams = selectTeamUnionInfos(condition, request.getSortType(), request.getLimit());
        final SliceNextInfo lastInfo = setNextInfo(teams, request.getLimit(), request.getSortType());
        final List<TeamUnionInfo> fitTeams = ensureMaxSize(teams, request.getLimit());

        return mapToYoungTeam(fitTeams, lastInfo);
    }

    private List<TeamUnionInfo> selectTeamUnionInfos(BooleanExpression condition, SortType sortType, Integer limit) {
        return select(Projections.fields(
                TeamUnionInfo.class,
                TU.globalId,
                TU.id,
                TU.teamType,
                TU.createdAt,
                TU.viewCount,
                TU.likeCount
                ))
                .from(TU)
                .where(condition)
                .orderBy(switch (sortType) {
                    case UPDATE_AT_DESC -> TU.createdAt.desc();
                    case VIEW_COUNT_DESC -> TU.viewCount.desc();
                    case LIKE_COUNT_DESC -> TU.likeCount.desc();
                }, TU.globalId.desc())
                .limit(limit + 1L)
                .fetch();
    }

    private BooleanExpression buildCursorCondition(
            UUID globalId, LocalDateTime date, Integer count,  SortType sortType) {

        return switch (sortType) {
            case UPDATE_AT_DESC -> buildCursorForDate(date, TU.createdAt, globalId);
            case VIEW_COUNT_DESC -> buildCursorForInt(count, TU.viewCount, globalId);
            case LIKE_COUNT_DESC -> buildCursorForInt(count, TU.likeCount, globalId);
        };
    }

    private BooleanExpression buildCursorForInt(Integer fieldValue, NumberPath<Integer> expr, UUID globalId) {
        if (fieldValue == null) return null;
        if (globalId == null) return expr.lt(fieldValue);
        return expr.lt(fieldValue).or(expr.eq(fieldValue).and(TU.globalId.lt(globalId)));
    }

    private BooleanExpression buildCursorForDate(LocalDateTime fieldValue, DateTimePath<LocalDateTime> expr, UUID globalId) {
        if (fieldValue == null) return null;
        if (globalId == null) return expr.lt(fieldValue);
        return expr.lt(fieldValue).or(expr.eq(fieldValue).and(TU.globalId.lt(globalId)));
    }

    private static SliceNextInfo setNextInfo(List<TeamUnionInfo> sortedTeams, Integer limit, SortType sortType) {
        if (sortedTeams.size() <= limit) {
            return SliceNextInfo.builder().hasNext(false).build();
        }
        final TeamUnionInfo last = sortedTeams.getLast();
        return switch (sortType) {
            case UPDATE_AT_DESC -> SliceNextInfo.builder()
                    .hasNext(true)
                    .globalId(last.getGlobalId())
                    .dateCursor(last.getDateCursor())
                    .sortType(sortType)
                    .build();
            case VIEW_COUNT_DESC, LIKE_COUNT_DESC -> SliceNextInfo.builder()
                    .hasNext(true)
                    .globalId(last.getGlobalId())
                    .countCursor(last.getCountCursor())
                    .sortType(sortType)
                    .build();
        };
    }


    public static <T> List<T> ensureMaxSize(List<T> unionTeams, Integer limit) {
        if (unionTeams.size() > limit) {
            return unionTeams.subList(0, limit);
        }
        return unionTeams;
    }

    private static TeamUnionSliceYoungInfo mapToYoungTeam(List<TeamUnionInfo> teams, SliceNextInfo last) {
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
}
