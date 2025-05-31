package backend.techeerzip.domain.projectTeam.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.common.util.DslBooleanBuilder;
import backend.techeerzip.domain.projectMember.entity.QProjectMember;
import backend.techeerzip.domain.projectTeam.dto.request.GetProjectTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectSliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectUserTeamsResponse;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.QProjectMainImage;
import backend.techeerzip.domain.projectTeam.entity.QProjectTeam;
import backend.techeerzip.domain.projectTeam.mapper.ProjectTeamMapper;
import backend.techeerzip.domain.projectTeam.type.CountSortOption;
import backend.techeerzip.domain.projectTeam.type.DateSortOption;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.global.entity.StatusCategory;

@Repository
public class ProjectTeamDslRepositoryImpl extends AbstractQuerydslRepository
        implements ProjectTeamDslRepository {

    private static final QProjectTeam PT = QProjectTeam.projectTeam;

    public ProjectTeamDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(ProjectTeam.class, em, factory);
    }

    private static BooleanExpression dateCursorConditionBuilder(LocalDateTime date, Long id) {
        if (date == null) return null;
        if (id == null) return PT.updatedAt.lt(date);
        return PT.updatedAt.lt(date).or(PT.updatedAt.eq(date).and(PT.id.lt(id)));
    }

    /**
     * count 기반 정렬 조건 및 커서 정보에 따라 페이징 조건을 생성합니다.
     *
     * @param sortType 정렬 타입 (VIEW_COUNT_DESC, LIKE_COUNT_DESC)
     * @param count 정렬 기준 카운트 값
     * @param id 커서용 프로젝트 팀 ID
     * @return BooleanExpression 페이징 조건
     */
    public static BooleanExpression countCursorConditionBuilder(
            SortType sortType, Integer count, Long id) {
        return switch (sortType) {
            case VIEW_COUNT_DESC -> buildCountCursor(PT.viewCount, count, id);
            case LIKE_COUNT_DESC -> buildCountCursor(PT.likeCount, count, id);
            default -> throw new UnsupportedOperationException("count 기반 sortType 아님");
        };
    }

    /**
     * count 기반 커서 조건을 생성합니다.
     *
     * <p>count 값이 동일할 경우 ID를 기준으로 추가 정렬 조건을 부여합니다.</p>
     *
     * @param field 정렬 기준 필드 (조회수, 좋아요 수 등)
     * @param count 기준 카운트
     * @param id 커서 ID
     * @return BooleanExpression 조건식
     */
    private static BooleanExpression buildCountCursor(
            NumberPath<Integer> field, Integer count, Long id) {
        if (count == null) return null;
        if (id == null) return field.lt(count);
        return field.lt(count).or(field.eq(count).and(PT.id.lt(id)));
    }

    /**
     * 날짜 기반 커서 페이징과 포지션 필터링, 모집/종료 여부를 포함한 조건을 생성합니다.
     *
     * @param id 커서 ID
     * @param dateTime 기준 날짜
     * @param isRecruited 모집 여부
     * @param isFinished 종료 여부
     * @param numTypes 포지션 조건 리스트
     * @return BooleanExpression 조합된 조건
     */
    private static BooleanExpression setBuilderWithPosAndDate(
            Long id,
            LocalDateTime dateTime,
            Boolean isRecruited,
            Boolean isFinished,
            List<PositionNumType> numTypes) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(dateCursorConditionBuilder(dateTime, id))
                .andIfNotNull(isRecruited, PT.isRecruited::eq)
                .andIfNotNull(isFinished, PT.isFinished::eq)
                .andIfNotNull(buildPositionFilter(numTypes))
                .and(PT.isDeleted.isFalse())
                .build();
    }

    /**
     * count 기반 커서 페이징과 포지션 필터링, 모집/종료 여부를 포함한 조건을 생성합니다.
     *
     * @param id 커서 ID
     * @param count 기준 count 값
     * @param isRecruited 모집 여부
     * @param isFinished 종료 여부
     * @param numTypes 포지션 조건 리스트
     * @param sortType 정렬 타입
     * @return BooleanExpression 조합된 조건
     */
    private static BooleanExpression setBuilderWithPosAndCount(
            Long id,
            Integer count,
            Boolean isRecruited,
            Boolean isFinished,
            List<PositionNumType> numTypes,
            SortType sortType) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(countCursorConditionBuilder(sortType, count, id))
                .andIfNotNull(isRecruited, PT.isRecruited::eq)
                .andIfNotNull(isFinished, PT.isFinished::eq)
                .andIfNotNull(buildPositionFilter(numTypes))
                .and(PT.isDeleted.isFalse())
                .build();
    }


    /**
     * 포지션 조건에 맞는 필터 BooleanExpression을 생성합니다.
     *
     * @param positionNumTypes 포지션 조건 리스트
     * @return BooleanExpression 조건문 또는 null
     */
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

    /**
     * 두 개의 BooleanExpression을 OR 조건으로 병합합니다.
     *
     * @param expression 기존 조건
     * @param condition 새로 추가할 조건
     * @return OR로 병합된 조건
     */
    private static BooleanExpression orCondition(
            BooleanExpression expression, BooleanExpression condition) {
        if (expression == null) {
            expression = condition;
        } else {
            expression = expression.or(condition);
        }
        return expression;
    }

    /**
     * 정렬 타입에 따라 커서 기반 페이징 쿼리를 실행합니다.
     *
     * @param query 정렬 조건과 필터가 포함된 요청 쿼리 객체
     * @return 조건에 맞는 ProjectTeam 리스트 (limit + 1개)
     * @throws IllegalArgumentException 정렬 타입이 count, date 모두 아닌 경우
     */
    public List<ProjectTeam> sliceTeams(GetProjectTeamsQuery query) {
        if (query.getSortType().isDate()) {
            return sliceTeamsByDate(query);
        }

        if (query.getSortType().isCount()) {
            return sliceTeamsByCount(query);
        }
        throw new IllegalArgumentException();
    }

    /**
     * 날짜 정렬 기반 커서 페이징으로 프로젝트 팀 리스트를 조회합니다.
     *
     * @param query 정렬 기준, 커서, 필터 정보가 포함된 쿼리 객체
     * @return 조건에 부합하는 ProjectTeam 리스트 (limit + 1개)
     */
    public List<ProjectTeam> sliceTeamsByDate(GetProjectTeamsQuery query) {
        final Long id = query.getIdCursor();
        final List<PositionNumType> numTypes = query.getPositionNumTypes();
        final SortType sortType = query.getSortType();
        final LocalDateTime dateCursor = query.getDateCursor();
        final int limit = query.getLimit();
        final Boolean isRecruited = query.getIsRecruited();
        final Boolean isFinished = query.getIsFinished();

        final BooleanExpression condition =
                setBuilderWithPosAndDate(id, dateCursor, isRecruited, isFinished, numTypes);

        return selectFrom(PT)
                .where(condition)
                .orderBy(DateSortOption.setOrder(sortType, TeamType.PROJECT))
                .limit(limit + 1L)
                .fetch();
    }

    /**
     * 조회수/좋아요 수 정렬 기반 커서 페이징으로 프로젝트 팀 리스트를 조회합니다.
     *
     * @param query 정렬 기준, 커서, 필터 정보가 포함된 쿼리 객체
     * @return 조건에 부합하는 ProjectTeam 리스트 (limit + 1개)
     */
    public List<ProjectTeam> sliceTeamsByCount(GetProjectTeamsQuery query) {
        final Long id = query.getIdCursor();
        final List<PositionNumType> numTypes = query.getPositionNumTypes();
        final SortType sortType = query.getSortType();
        final Integer countCursor = query.getCountCursor();
        final int limit = query.getLimit();
        final boolean isRecruited = query.getIsRecruited();
        final boolean isFinished = query.getIsFinished();

        final BooleanExpression condition =
                setBuilderWithPosAndCount(
                        id, countCursor, isRecruited, isFinished, numTypes, sortType);

        return selectFrom(PT)
                .where(condition)
                .orderBy(CountSortOption.setOrder(sortType.name(), TeamType.PROJECT))
                .limit(limit + 1L)
                .fetch();
    }
}
