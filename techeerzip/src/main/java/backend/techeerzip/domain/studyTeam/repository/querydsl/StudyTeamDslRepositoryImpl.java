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
import backend.techeerzip.domain.studyTeam.dto.response.StudySliceTeamsResponse;
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

    /**
     * 날짜 기반 커서 조건을 적용하여 스터디 팀을 조회합니다.
     * 정렬 기준: updatedAt DESC, id DESC
     *
     * @param query 요청 필터 및 커서 정보
     * @return 커서 기준 정렬된 StudyTeam 리스트 (limit + 1개)
     */
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

    /**
     * 조회수 또는 좋아요 수 기반 커서 조건을 적용하여 스터디 팀을 조회합니다.
     * 정렬 기준: viewCount DESC or likeCount DESC, 이후 id DESC
     *
     * @param query 요청 필터 및 커서 정보
     * @return 커서 기준 정렬된 StudyTeam 리스트 (limit + 1개)
     */
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

    /**
     * 정렬 기준에 따라 스터디 팀 커서 페이징을 수행합니다.
     *
     * @param query 정렬 타입, 커서, 필터링 정보가 포함된 요청 객체
     * @return 조건에 맞는 StudyTeam 리스트 (limit + 1개)
     */
    public List<StudyTeam> sliceTeams(GetStudyTeamsQuery query) {

        if (query.getSortType().isDate()) {
            return sliceTeamsByDate(query);
        }
        if (query.getSortType().isCount()) {
            return sliceTeamsByCount(query);
        }
        throw new IllegalArgumentException();
    }

    /**
     * 날짜 기반 커서 조건 생성기: updatedAt < date 또는 (updatedAt == date and id < idCursor)
     *
     * @param date 기준 날짜
     * @param id 커서 기준 ID
     * @return BooleanExpression 조건
     */
    private static BooleanExpression dateCursorConditionBuilder(LocalDateTime date, Long id) {
        if (date == null) return null;
        if (id == null) return ST.updatedAt.lt(date);
        return ST.updatedAt.lt(date).or(ST.updatedAt.eq(date).and(ST.id.lt(id)));
    }

    /**
     * 수치 기반 커서 조건 생성기: field < count 또는 (field == count and id < idCursor)
     *
     * @param field 기준 필드 (조회수, 좋아요 수)
     * @param count 기준 값
     * @param id 커서 기준 ID
     * @return BooleanExpression 조건
     */
    private static BooleanExpression buildCountCursor(
            NumberPath<Integer> field, Integer count, Long id) {
        if (count == null) return null;
        if (id == null) return field.lt(count);
        return field.lt(count).or(field.eq(count).and(ST.id.lt(id)));
    }

    /**
     * 정렬 타입에 따라 count 기반 커서 조건을 생성합니다.
     *
     * @param sortType 정렬 타입
     * @param count 기준 값
     * @param id 커서 기준 ID
     * @return BooleanExpression 조건
     */
    public static BooleanExpression countCursorConditionBuilder(
            SortType sortType, Integer count, Long id) {
        return switch (sortType) {
            case VIEW_COUNT_DESC -> buildCountCursor(ST.viewCount, count, id);
            case LIKE_COUNT_DESC -> buildCountCursor(ST.likeCount, count, id);
            default -> throw new UnsupportedOperationException("count 기반 sortType 아님");
        };
    }

    /**
     * 날짜 기반 커서 조건과 필터링 조건을 조합합니다.
     *
     * @param id 커서 ID
     * @param dateTime 기준 날짜
     * @param isRecruited 모집 여부
     * @param isFinished 종료 여부
     * @return BooleanExpression 조합 조건
     */
    private static BooleanExpression setBuilderWithAndDate(
            Long id, LocalDateTime dateTime, Boolean isRecruited, Boolean isFinished) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(dateCursorConditionBuilder(dateTime, id))
                .andIfNotNull(isRecruited, ST.isRecruited::eq)
                .andIfNotNull(isFinished, ST.isFinished::eq)
                .and(ST.isDeleted.isFalse())
                .build();
    }

    /**
     * count 기반 커서 조건과 필터링 조건을 조합합니다.
     *
     * @param id 커서 ID
     * @param count 기준 count 값
     * @param isRecruited 모집 여부
     * @param isFinished 종료 여부
     * @param sortType 정렬 타입
     * @return BooleanExpression 조합 조건
     */
    private static BooleanExpression setBuilderWithAndCount(
            Long id, Integer count, Boolean isRecruited, Boolean isFinished, SortType sortType) {
        return DslBooleanBuilder.builder()
                .andIfNotNull(countCursorConditionBuilder(sortType, count, id))
                .andIfNotNull(isRecruited, ST.isRecruited::eq)
                .andIfNotNull(isFinished, ST.isFinished::eq)
                .and(ST.isDeleted.isFalse())
                .build();
    }

    /**
     * 특정 ID 리스트를 기준으로 조건에 맞는 스터디 팀 리스트를 조회합니다.
     *
     * @param keys 대상 ID 리스트
     * @param isRecruited 모집 여부
     * @param isFinished 종료 여부
     * @return StudySliceTeamsResponse 리스트
     */
    public List<StudySliceTeamsResponse> findManyYoungTeamById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished) {
        final BooleanExpression expression =
                setBuilder(isRecruited, isFinished).and(ST.id.in(keys));
        List<StudyTeam> teams = selectFrom(ST).where(expression).fetch();
        return teams.stream().map(StudyTeamMapper::toGetAllResponse).toList();
    }
}
