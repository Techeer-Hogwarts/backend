package backend.techeerzip.domain.studyTeam.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.common.util.DslBooleanBuilder;
import backend.techeerzip.domain.projectTeam.dto.request.GetStudyTeamsQuery;
import backend.techeerzip.domain.projectTeam.type.CountSortOption;
import backend.techeerzip.domain.projectTeam.type.DateSortOption;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.domain.studyTeam.dto.StudySliceTeamsResponse;
import backend.techeerzip.domain.studyTeam.entity.QStudyTeam;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;
import backend.techeerzip.domain.studyTeam.mapper.StudyTeamMapper;

@Repository
public class StudyTeamDslRepositoryImpl extends AbstractQuerydslRepository
        implements StudyTeamDslRepository {

    private static final QStudyTeam ST = QStudyTeam.studyTeam;

    public StudyTeamDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(StudyTeam.class, em, factory);
    }

    private static BooleanExpression setBuilder(Boolean isRecruited, Boolean isFinished) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(isRecruited, ST.isRecruited::eq)
                .andIfNotNull(isFinished, ST.isFinished::eq)
                .and(ST.isDeleted.isFalse())
                .build();
    }

    public List<StudyTeam> sliceTeamsByDate(GetStudyTeamsQuery query) {
        final Long id = query.getIdCursor();
        final SortType sortType = query.getSortType();
        final LocalDateTime dateCursor = query.getDateCursor();
        final int limit = query.getLimit();
        final boolean isRecruited = query.getIsRecruited();
        final boolean isFinished = query.getIsFinished();

        final BooleanExpression expression =
                setBuilderWithAndDate(id, dateCursor, isRecruited, isFinished);

        return selectFrom(ST)
                .where(expression)
                .orderBy(DateSortOption.setOrder(sortType, TeamType.STUDY))
                .limit(limit + 1L)
                .fetch();
    }

    public List<StudyTeam> sliceTeamsByCount(GetStudyTeamsQuery query) {
        final Long id = query.getIdCursor();
        final SortType sortType = query.getSortType();
        final Integer countCursor = query.getCountCursor();
        final int limit = query.getLimit();
        final boolean isRecruited = query.getIsRecruited();
        final boolean isFinished = query.getIsFinished();
        final BooleanExpression expression =
                setBuilderWithAndCount(id, countCursor, isRecruited, isFinished, sortType);

        return selectFrom(ST)
                .where(expression)
                .orderBy(CountSortOption.setOrder(sortType.name(), TeamType.STUDY))
                .limit(limit + 1L)
                .fetch();
    }

    public List<StudyTeam> sliceTeams(GetStudyTeamsQuery query) {

        if (query.getSortType().isDate()) {
            return sliceTeamsByDate(query);
        }
        if (query.getSortType().isCount()) {
            return sliceTeamsByCount(query);
        }
        throw new IllegalArgumentException();
    }

    private static BooleanExpression dateCursorConditionBuilder(LocalDateTime date, Long id) {
        if (date == null) return null;
        if (id == null) return ST.updatedAt.lt(date);
        return ST.updatedAt.lt(date).or(ST.updatedAt.eq(date).and(ST.id.lt(id)));
    }

    private static BooleanExpression buildCountCursor(
            NumberPath<Integer> field, Integer count, Long id) {
        if (count == null) return null;
        if (id == null) return field.lt(count);
        return field.lt(count).or(field.eq(count).and(ST.id.lt(id)));
    }

    public static BooleanExpression countCursorConditionBuilder(
            SortType sortType, Integer count, Long id) {
        return switch (sortType) {
            case VIEW_COUNT_DESC -> buildCountCursor(ST.viewCount, count, id);
            case LIKE_COUNT_DESC -> buildCountCursor(ST.likeCount, count, id);
            default -> throw new UnsupportedOperationException("count 기반 sortType 아님");
        };
    }

    private static BooleanExpression setBuilderWithAndDate(
            Long id, LocalDateTime dateTime, Boolean isRecruited, Boolean isFinished) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(dateCursorConditionBuilder(dateTime, id))
                .andIfNotNull(isRecruited, ST.isRecruited::eq)
                .andIfNotNull(isFinished, ST.isFinished::eq)
                .and(ST.isDeleted.isFalse())
                .build();
    }

    private static BooleanExpression setBuilderWithAndCount(
            Long id, Integer count, Boolean isRecruited, Boolean isFinished, SortType sortType) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(countCursorConditionBuilder(sortType, count, id))
                .andIfNotNull(isRecruited, ST.isRecruited::eq)
                .andIfNotNull(isFinished, ST.isFinished::eq)
                .and(ST.isDeleted.isFalse())
                .build();
    }

    public List<StudySliceTeamsResponse> findManyYoungTeamById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished) {
        final BooleanExpression expression =
                setBuilder(isRecruited, isFinished).and(ST.id.in(keys));
        List<StudyTeam> teams = selectFrom(ST).where(expression).fetch();
        return teams.stream().map(StudyTeamMapper::toGetAllResponse).toList();
    }

    public List<StudySliceTeamsResponse> sliceYoungTeamByDate(
            UUID id,
            SortType sortType,
            LocalDateTime dateCursor,
            int limit,
            boolean isRecruited,
            boolean isFinished) {
        return List.of();
    }
}
