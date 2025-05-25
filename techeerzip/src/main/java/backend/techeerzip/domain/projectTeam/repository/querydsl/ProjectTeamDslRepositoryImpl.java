package backend.techeerzip.domain.projectTeam.repository.querydsl;

import backend.techeerzip.domain.projectTeam.dto.request.GetProjectTeamsQuery;
import backend.techeerzip.domain.projectTeam.type.DateSortOption;
import backend.techeerzip.domain.projectTeam.type.CountSortOption;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import com.querydsl.core.types.dsl.NumberPath;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.common.util.DslBooleanBuilder;
import backend.techeerzip.domain.projectMember.entity.QProjectMember;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectSliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectUserTeamsResponse;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.QProjectMainImage;
import backend.techeerzip.domain.projectTeam.entity.QProjectTeam;
import backend.techeerzip.domain.projectTeam.mapper.ProjectTeamMapper;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.global.entity.StatusCategory;

@Repository
public class ProjectTeamDslRepositoryImpl extends AbstractQuerydslRepository
        implements ProjectTeamDslRepository {

    private static final QProjectTeam PT = QProjectTeam.projectTeam;
    private static final QProjectMember PM = QProjectMember.projectMember;
    private static final QProjectMainImage PMI = QProjectMainImage.projectMainImage;

    public ProjectTeamDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(ProjectTeam.class, em, factory);
    }

    private static BooleanExpression setBuilderWithPosition(
            Boolean isRecruited, Boolean isFinished, List<PositionNumType> numTypes) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(isRecruited, PT.isRecruited::eq)
                .andIfNotNull(isFinished, PT.isFinished::eq)
                .andIfNotNull(buildPositionFilter(numTypes))
                .and(PT.isDeleted.isFalse())
                .build();
    }
    private static BooleanExpression dateCursorConditionBuilder(LocalDateTime date, Long id) {
        if (date == null) return null;
        if (id == null) return PT.updatedAt.lt(date);
        return PT.updatedAt.lt(date)
                .or(PT.updatedAt.eq(date).and(PT.id.lt(id)));
    }


    public static BooleanExpression countCursorConditionBuilder(SortType sortType, Integer count, Long id) {
        return switch (sortType) {
            case VIEW_COUNT_DESC -> buildCountCursor(PT.viewCount, count, id);
            case LIKE_COUNT_DESC -> buildCountCursor(PT.likeCount, count, id);
            default -> throw new UnsupportedOperationException("count 기반 sortType 아님");
        };
    }

    private static BooleanExpression buildCountCursor(NumberPath<Integer> field, Integer count, Long id) {
        if (count == null) return null;
        if (id == null) return field.lt(count);
        return field.lt(count).or(field.eq(count).and(PT.id.lt(id)));
    }


    private static BooleanExpression setBuilderWithPosAndDate(Long id, LocalDateTime dateTime,
            Boolean isRecruited, Boolean isFinished, List<PositionNumType> numTypes) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(dateCursorConditionBuilder(dateTime, id))
                .andIfNotNull(isRecruited, PT.isRecruited::eq)
                .andIfNotNull(isFinished, PT.isFinished::eq)
                .andIfNotNull(buildPositionFilter(numTypes))
                .and(PT.isDeleted.isFalse())
                .build();
    }

    private static BooleanExpression setBuilderWithPosAndCount(Long id, Integer count,
            Boolean isRecruited, Boolean isFinished, List<PositionNumType> numTypes, SortType sortType) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(countCursorConditionBuilder(sortType, count, id))
                .andIfNotNull(isRecruited, PT.isRecruited::eq)
                .andIfNotNull(isFinished, PT.isFinished::eq)
                .andIfNotNull(buildPositionFilter(numTypes))
                .and(PT.isDeleted.isFalse())
                .build();
    }

    private static BooleanExpression setBuilder(Boolean isRecruited, Boolean isFinished) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(isRecruited, PT.isRecruited::eq)
                .andIfNotNull(isFinished, PT.isFinished::eq)
                .and(PT.isDeleted.isFalse())
                .build();
    }

    public static BooleanExpression buildPositionFilter(List<PositionNumType> positionNumTypes) {
        if (positionNumTypes == null || positionNumTypes.isEmpty()) {
            return null;
        }
        BooleanExpression expression = null;
        for (PositionNumType p : positionNumTypes) {
            BooleanExpression condition = p.getField(PT).gt(0);
            expression = orCondition(expression, condition);
        }
        return expression;
    }

    private static BooleanExpression orCondition(
            BooleanExpression expression, BooleanExpression condition) {
        if (expression == null) {
            expression = condition;
        } else {
            expression = expression.or(condition);
        }
        return expression;
    }

    public List<ProjectTeam> sliceYoungTeams(GetProjectTeamsQuery query) {
        if (query.getSortType().isDate()) {
            return sliceYoungTeamByDate(query);
        }

        if (query.getSortType().isCount()) {
            return sliceYoungTeamByCount(query);
        }
        throw new IllegalArgumentException();
    }

    public List<ProjectSliceTeamsResponse> findManyYoungTeamById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished) {
        final BooleanExpression condition = setBuilder(isRecruited, isFinished).and(PT.id.in(keys));
        final List<ProjectTeam> teams =
                selectFrom(PT).where(condition).orderBy(PT.createdAt.desc()).fetch();
        return teams.stream().map(ProjectTeamMapper::toGetAllResponse).toList();
    }

    public List<ProjectTeam> sliceYoungTeamByDate(GetProjectTeamsQuery query) {
        final Long id = query.getIdCursor();
        final List<PositionNumType> numTypes = query.getPositionNumTypes();
        final SortType sortType = query.getSortType();
        final LocalDateTime dateCursor = query.getDateCursor();
        final int limit = query.getLimit();
        final boolean isRecruited = query.getIsRecruited();
        final boolean isFinished = query.getIsFinished();

        final BooleanExpression condition =
                setBuilderWithPosAndDate(id, dateCursor, isRecruited, isFinished, numTypes);

        return selectFrom(PT)
                        .where(condition)
                        .orderBy(DateSortOption.setOrder(sortType.name(), TeamType.PROJECT))
                        .limit(limit + 1)
                        .fetch();
    }

    public List<ProjectTeam> sliceYoungTeamByCount(GetProjectTeamsQuery query) {
        final Long id = query.getIdCursor();
        final List<PositionNumType> numTypes = query.getPositionNumTypes();
        final SortType sortType = query.getSortType();
        final Integer countCursor = query.getCountCursor();
        final int limit = query.getLimit();
        final boolean isRecruited = query.getIsRecruited();
        final boolean isFinished = query.getIsFinished();

        final BooleanExpression condition =
                setBuilderWithPosAndCount(id, countCursor, isRecruited, isFinished, numTypes, sortType);

        return selectFrom(PT)
                        .where(condition)
                        .orderBy(CountSortOption.setOrder(sortType.name(), TeamType.PROJECT))
                        .limit(limit + 1)
                        .fetch();
    }

    public List<ProjectUserTeamsResponse> findAllTeamsByUserId(Long userId) {
        return selectFrom(PT)
                .leftJoin(PT.projectMembers, PM)
                .leftJoin(PT.mainImages, PMI)
                .where(
                        PT.isDeleted.eq(false),
                        PM.user.id.eq(userId),
                        PM.isDeleted.eq(false),
                        PM.status.eq(StatusCategory.APPROVED))
                .transform(
                        GroupBy.groupBy(PT.id)
                                .list(
                                        Projections.constructor(
                                                ProjectUserTeamsResponse.class,
                                                PT.id,
                                                PM.user.name,
                                                GroupBy.list(PMI.imageUrl).as("mainImageUrl"))));
    }


}
