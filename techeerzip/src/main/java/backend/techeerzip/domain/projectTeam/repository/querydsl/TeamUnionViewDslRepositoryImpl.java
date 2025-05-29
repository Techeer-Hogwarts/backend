package backend.techeerzip.domain.projectTeam.repository.querydsl;

import backend.techeerzip.domain.projectTeam.type.TeamType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.common.util.DslBooleanBuilder;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.response.SliceNextCursor;
import backend.techeerzip.domain.projectTeam.dto.response.TeamUnionSliceResult;
import backend.techeerzip.domain.projectTeam.dto.response.UnionSliceTeam;
import backend.techeerzip.domain.projectTeam.entity.QTeamUnionView;
import backend.techeerzip.domain.projectTeam.entity.TeamUnionView;
import backend.techeerzip.domain.projectTeam.type.SortType;

@Repository
public class TeamUnionViewDslRepositoryImpl extends AbstractQuerydslRepository
        implements TeamUnionViewDslRepository {

    private static final QTeamUnionView TU = QTeamUnionView.teamUnionView;

    protected TeamUnionViewDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(TeamUnionView.class, em, factory);
    }

    public TeamUnionSliceResult fetchSliceTeams(GetTeamsQuery request) {
        final BooleanExpression condition =
                DslBooleanBuilder.builder()
                        .andIfNotNull(request.getIsRecruited(), TU.isRecruited::eq)
                        .andIfNotNull(request.getIsFinished(), TU.isFinished::eq)
                        .and(TU.isDeleted.isFalse())
                        .andIfNotNull(
                                buildCursorCondition(request))
                        .build();

        final List<UnionSliceTeam> teams =
                selectTeamUnionInfos(condition, request.getSortType(), request.getLimit());
        final SliceNextCursor lastInfo =
                setNextInfo(teams, request.getLimit(), request.getSortType());
        final List<UnionSliceTeam> slicedInfos = ensureMaxSize(teams, request.getLimit());

        return new TeamUnionSliceResult(slicedInfos, lastInfo);
    }

    private List<UnionSliceTeam> selectTeamUnionInfos(
            BooleanExpression condition, SortType sortType, Integer limit) {
        return select(
                        Projections.fields(
                                UnionSliceTeam.class,
                                TU.id,
                                TU.teamType,
                                TU.updatedAt,
                                TU.viewCount,
                                TU.likeCount))
                .from(TU)
                .where(condition)
                .orderBy(
                        switch (sortType) {
                            case UPDATE_AT_DESC -> TU.updatedAt.desc();
                            case VIEW_COUNT_DESC -> TU.viewCount.desc();
                            case LIKE_COUNT_DESC -> TU.likeCount.desc();
                        },
                        TU.id.desc())
                .limit(limit + 1L)
                .fetch();
    }

    private BooleanExpression buildCursorCondition(GetTeamsQuery request) {
        final Long id = request.getId();
        final LocalDateTime date = request.getDateCursor();
        final Integer count = request.getCountCursor();
        final SortType sortType = request.getSortType();
        final LocalDateTime createAt = Optional.ofNullable(request.getCreateAt())
                .orElse(LocalDateTime.MAX);
        return switch (sortType) {
            case UPDATE_AT_DESC -> buildCursorForDate(date, TU.updatedAt, id);
            case VIEW_COUNT_DESC -> buildCursorForInt(count, TU.viewCount, createAt, id);
            case LIKE_COUNT_DESC -> buildCursorForInt(count, TU.likeCount, createAt, id);
        };
    }

    private BooleanExpression buildCursorForInt(
            Integer fieldValue, NumberPath<Integer> expr, LocalDateTime createdAt, Long id) {
        if (fieldValue == null || createdAt == null || id == null) return null;

        return expr.lt(fieldValue)
                .or(expr.eq(fieldValue).and(
                        TU.createdAt.lt(createdAt)
                                .or(TU.createdAt.eq(createdAt).and(TU.id.lt(id)))
                ));
    }

    private BooleanExpression buildCursorForDate(
            LocalDateTime fieldValue, DateTimePath<LocalDateTime> expr, Long id) {
        if (fieldValue == null || id == null) return null;

        return expr.lt(fieldValue)
                .or(expr.eq(fieldValue).and(TU.id.lt(id)));
    }

    private static SliceNextCursor setNextInfo(
            List<UnionSliceTeam> sortedTeams, Integer limit, SortType sortType) {
        if (sortedTeams.size() <= limit) {
            return SliceNextCursor.builder().hasNext(false).build();
        }
        final UnionSliceTeam last = sortedTeams.getLast();
        return switch (sortType) {
            case UPDATE_AT_DESC ->
                    SliceNextCursor.builder()
                            .hasNext(true)
                            .id(last.getId())
                            .dateCursor(last.getUpdatedAt())
                            .sortType(sortType)
                            .build();
            case VIEW_COUNT_DESC ->
                    SliceNextCursor.builder()
                            .hasNext(true)
                            .id(last.getId())
                            .countCursor(last.getViewCount())
                            .sortType(sortType)
                            .build();
            case LIKE_COUNT_DESC ->
                    SliceNextCursor.builder()
                            .hasNext(true)
                            .id(last.getId())
                            .countCursor(last.getLikeCount())
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
}
