package backend.techeerzip.domain.projectTeam.repository.querydsl;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import backend.techeerzip.domain.common.repository.AbstractQuerydslRepository;
import backend.techeerzip.domain.common.util.DslBooleanBuilder;
import backend.techeerzip.domain.projectTeam.dto.request.GetProjectTeamsQuery;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.QProjectTeam;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamInvalidSortType;
import backend.techeerzip.domain.projectTeam.exception.TeamInvalidSliceQueryException;
import backend.techeerzip.domain.projectTeam.type.CountSortOption;
import backend.techeerzip.domain.projectTeam.type.DateSortOption;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info(
                "ProjectTeam countCursorConditionBuilder: 커서 조건 생성 시작 - sortType={}, count={}, id={}",
                sortType,
                count,
                id);
        return switch (sortType) {
            case VIEW_COUNT_DESC -> buildCountCursor(PT.viewCount, count, id);
            case LIKE_COUNT_DESC -> buildCountCursor(PT.likeCount, count, id);
            default -> {
                log.error(
                        "ProjectTeam countCursorConditionBuilder: 유효하지 않은 정렬 방식 - sortType={}",
                        sortType);
                throw new ProjectTeamInvalidSortType();
            }
        };
    }

    /**
     * count 기반 커서 조건을 생성합니다.
     *
     * <p>count 값이 동일할 경우 ID를 기준으로 추가 정렬 조건을 부여합니다.
     *
     * @param field 정렬 기준 필드 (조회수, 좋아요 수 등)
     * @param count 기준 카운트
     * @param id 커서 ID
     * @return BooleanExpression 조건식
     */
    private static BooleanExpression buildCountCursor(
            NumberPath<Integer> field, Integer count, Long id) {
        log.info("ProjectTeam buildCountCursor: count 기준 커서 조건 생성 - count={}, id={}", count, id);

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
        log.info(
                "ProjectTeam setBuilderWithPosAndDate: 조건 조립 시작 - id={}, dateCursor={}, isRecruited={}, isFinished={}, positionSize={}",
                id,
                dateTime,
                isRecruited,
                isFinished,
                (numTypes != null ? numTypes.size() : 0));
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
        log.info(
                "ProjectTeam setBuilderWithPosAndCount: 조건 조립 시작 - id={}, countCursor={}, isRecruited={}, isFinished={}, sortType={}, positionSize={}",
                id,
                count,
                isRecruited,
                isFinished,
                sortType,
                (numTypes != null ? numTypes.size() : 0));
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
        log.info("ProjectTeam buildPositionFilter: 포지션 필터 생성 - size={}", positionNumTypes.size());

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
        log.info("ProjectTeam sliceTeams: 커서 기반 팀 조회 시작 - sortType={}", query.getSortType());

        if (query.getSortType().isDate()) {
            return sliceTeamsByDate(query);
        }

        if (query.getSortType().isCount()) {
            return sliceTeamsByCount(query);
        }
        log.error("ProjectTeam sliceTeams: 조회 쿼리 오류 - sortType={}", query.getSortType());
        throw new TeamInvalidSliceQueryException();
    }

    /**
     * 날짜 정렬 기반 커서 페이징으로 프로젝트 팀 리스트를 조회합니다.
     *
     * @param query 정렬 기준, 커서, 필터 정보가 포함된 쿼리 객체
     * @return 조건에 부합하는 ProjectTeam 리스트 (limit + 1개)
     */
    public List<ProjectTeam> sliceTeamsByDate(GetProjectTeamsQuery query) {
        log.info(
                "ProjectTeam sliceTeamsByDate: 날짜 정렬 기준 조회 시작 - limit={}, dateCursor={}",
                query.getLimit(),
                query.getDateCursor());
        final BooleanExpression condition =
                setBuilderWithPosAndDate(
                        query.getIdCursor(),
                        query.getDateCursor(),
                        query.getIsRecruited(),
                        query.getIsFinished(),
                        query.getPositionNumTypes());
        final OrderSpecifier<LocalDateTime> setOrder =
                DateSortOption.setOrder(query.getSortType(), TeamType.PROJECT);
        log.info(
                "ProjectTeam sliceTeamsByDate: 조건식 정보 - condition={}",
                condition != null ? condition.toString() : "null");
        log.info(
                "ProjectTeam sliceTeamsByDate: 정렬 정보 - condition={}",
                setOrder != null ? setOrder.toString() : "null");

        final List<ProjectTeam> result =
                selectFrom(PT)
                        .where(condition)
                        .orderBy(setOrder)
                        .limit(query.getLimit() + 1L)
                        .fetch();

        log.info("ProjectTeam sliceTeamsByDate: 조회 완료 - resultSize={}", result.size());
        return result;
    }

    /**
     * 조회수/좋아요 수 정렬 기반 커서 페이징으로 프로젝트 팀 리스트를 조회합니다.
     *
     * @param query 정렬 기준, 커서, 필터 정보가 포함된 쿼리 객체
     * @return 조건에 부합하는 ProjectTeam 리스트 (limit + 1개)
     */
    public List<ProjectTeam> sliceTeamsByCount(GetProjectTeamsQuery query) {
        log.info(
                "ProjectTeam sliceTeamsByCount: 카운트 정렬 기준 조회 시작 - limit={}, countCursor={}",
                query.getLimit(),
                query.getCountCursor());
        final BooleanExpression condition =
                setBuilderWithPosAndCount(
                        query.getIdCursor(),
                        query.getCountCursor(),
                        query.getIsRecruited(),
                        query.getIsFinished(),
                        query.getPositionNumTypes(),
                        query.getSortType());

        final OrderSpecifier<LocalDateTime> setOrder =
                DateSortOption.setOrder(query.getSortType(), TeamType.PROJECT);
        log.info(
                "ProjectTeam sliceTeamsByCount: 조건식 정보 - condition={}",
                condition != null ? condition.toString() : "null");
        log.info(
                "ProjectTeam sliceTeamsByCount: 정렬 정보 - condition={}",
                setOrder != null ? setOrder.toString() : "null");

        final List<ProjectTeam> result =
                selectFrom(PT)
                        .where(condition)
                        .orderBy(CountSortOption.setOrder(query.getSortType(), TeamType.PROJECT))
                        .limit(query.getLimit() + 1L)
                        .fetch();

        log.info("ProjectTeam sliceTeamsByCount: 조회 완료 - resultSize={}", result.size());
        return result;
    }
}
