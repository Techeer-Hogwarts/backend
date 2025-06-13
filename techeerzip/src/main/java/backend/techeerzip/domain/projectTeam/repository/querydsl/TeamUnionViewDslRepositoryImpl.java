package backend.techeerzip.domain.projectTeam.repository.querydsl;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class TeamUnionViewDslRepositoryImpl extends AbstractQuerydslRepository
        implements TeamUnionViewDslRepository {

    private static final QTeamUnionView TU = QTeamUnionView.teamUnionView;

    protected TeamUnionViewDslRepositoryImpl(EntityManager em, JPAQueryFactory factory) {
        super(TeamUnionView.class, em, factory);
    }

    /**
     * 팀 뷰에서 정렬, 커서, 필터 조건에 따라 페이징된 팀 목록을 조회합니다.
     *
     * @param request 정렬 조건, 필터, 커서가 포함된 요청 객체
     * @return 페이징된 팀 정보와 다음 커서 정보를 포함한 결과 객체
     */
    public TeamUnionSliceResult fetchSliceTeams(GetTeamsQuery request) {
        log.info(
                "TeamUnionView fetchSliceTeams: 커서 기반 팀 조회 시작 - sortType={}, limit={}, isRecruited={}, isFinished={}",
                request.getSortType(),
                request.getLimit(),
                request.getIsRecruited(),
                request.getIsFinished());

        final BooleanExpression condition =
                DslBooleanBuilder.builder()
                        .andIfNotNull(request.getIsRecruited(), TU.isRecruited::eq)
                        .andIfNotNull(request.getIsFinished(), TU.isFinished::eq)
                        .and(TU.isDeleted.isFalse())
                        .andIfNotNull(buildCursorCondition(request))
                        .build();
        log.info(
                "TeamUnionView fetchSliceTeams: 조건식 정보 - condition={}",
                condition != null ? condition.toString() : "null");

        final List<UnionSliceTeam> teams =
                selectTeamUnionInfos(condition, request.getSortType(), request.getLimit());
        log.info("TeamUnionView fetchSliceTeams: 팀 데이터 조회 완료 - fetchedSize={}", teams.size());

        final SliceNextCursor lastInfo =
                setNextInfo(teams, request.getLimit(), request.getSortType());
        log.info(
                "TeamUnionView fetchSliceTeams: 커서 정보 생성 완료 - hasNext={}, nextCursorId={}",
                lastInfo.getHasNext(),
                lastInfo.getId());

        final List<UnionSliceTeam> slicedInfos = ensureMaxSize(teams, request.getLimit());

        return new TeamUnionSliceResult(slicedInfos, lastInfo);
    }

    /**
     * 주어진 조건과 정렬 기준에 따라 TeamUnionView에서 데이터를 조회합니다.
     *
     * @param condition 조회 조건
     * @param sortType 정렬 기준
     * @param limit 조회할 최대 개수
     * @return 조회된 UnionSliceTeam 리스트 (limit + 1개까지 반환 가능)
     */
    private List<UnionSliceTeam> selectTeamUnionInfos(
            BooleanExpression condition, SortType sortType, Integer limit) {
        log.info(
                "TeamUnionView selectTeamUnionInfos: 팀 뷰 조회 실행 - sortType={}, limit={}",
                sortType,
                limit);

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

    /**
     * 요청의 정렬 기준에 따라 적절한 커서 조건을 생성합니다.
     *
     * @param request 요청 객체
     * @return BooleanExpression 형태의 커서 조건
     */
    private BooleanExpression buildCursorCondition(GetTeamsQuery request) {
        final Long id = request.getId();
        final LocalDateTime date = request.getDateCursor();
        final Integer count = request.getCountCursor();
        final SortType sortType = request.getSortType();
        final LocalDateTime updateAt =
                Optional.ofNullable(request.getDateCursor())
                        .orElse(LocalDateTime.of(9999, 12, 31, 23, 59));
        log.info(
                "TeamUnionView buildCursorCondition: 커서 조건 생성 시작 - sortType={}, id={}, dateCursor={}, countCursor={}",
                sortType,
                id,
                date,
                count);

        return switch (sortType) {
            case UPDATE_AT_DESC -> buildCursorForDate(date, TU.updatedAt, id);
            case VIEW_COUNT_DESC -> buildCursorForInt(count, TU.viewCount, updateAt, id);
            case LIKE_COUNT_DESC -> buildCursorForInt(count, TU.likeCount, updateAt, id);
        };
    }

    /**
     * 조회수/좋아요 수 기반 정렬 시 커서 조건을 생성합니다. 동일한 count 값일 경우 createdAt과 ID를 비교하여 커서 정렬을 보조합니다.
     *
     * @param fieldValue 기준이 되는 count 값
     * @param expr 정렬에 사용할 필드
     * @param updateAt 생성 시간 기준
     * @param id 기준 ID
     * @return BooleanExpression 형태의 커서 조건
     */
    private BooleanExpression buildCursorForInt(
            Integer fieldValue, NumberPath<Integer> expr, LocalDateTime updateAt, Long id) {
        if (fieldValue == null || updateAt == null || id == null) return null;

        return expr.lt(fieldValue)
                .or(
                        expr.eq(fieldValue)
                                .and(
                                        TU.updatedAt
                                                .lt(updateAt)
                                                .or(TU.updatedAt.eq(updateAt).and(TU.id.lt(id)))));
    }

    /**
     * 날짜 기반 정렬 시 커서 조건을 생성합니다. 동일한 날짜일 경우 ID를 기준으로 정렬을 보조합니다.
     *
     * @param fieldValue 날짜 커서 값
     * @param expr 정렬에 사용할 필드
     * @param id 기준 ID
     * @return BooleanExpression 형태의 커서 조건
     */
    private BooleanExpression buildCursorForDate(
            LocalDateTime fieldValue, DateTimePath<LocalDateTime> expr, Long id) {
        if (fieldValue == null || id == null) return null;

        return expr.lt(fieldValue).or(expr.eq(fieldValue).and(TU.id.lt(id)));
    }

    /**
     * 페이징 결과 리스트에서 마지막 요소를 기준으로 다음 커서를 생성합니다.
     *
     * @param sortedTeams 정렬된 팀 리스트
     * @param limit 요청한 최대 개수
     * @param sortType 정렬 기준
     * @return 다음 페이지 요청에 사용할 커서 정보
     */
    private static SliceNextCursor setNextInfo(
            List<UnionSliceTeam> sortedTeams, Integer limit, SortType sortType) {
        if (sortedTeams.size() <= limit) {
            log.info(
                    "TeamUnionView setNextInfo: 다음 페이지 없음 - size={}, limit={}",
                    sortedTeams.size(),
                    limit);
            return SliceNextCursor.builder().hasNext(false).build();
        }
        final UnionSliceTeam last = sortedTeams.getLast();
        log.info(
                "TeamUnionView setNextInfo: 다음 페이지 있음 - lastId={}, sortType={}",
                last.getId(),
                sortType);

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
                            .dateCursor(last.getUpdatedAt())
                            .sortType(sortType)
                            .build();
            case LIKE_COUNT_DESC ->
                    SliceNextCursor.builder()
                            .hasNext(true)
                            .id(last.getId())
                            .countCursor(last.getLikeCount())
                            .dateCursor(last.getUpdatedAt())
                            .sortType(sortType)
                            .build();
        };
    }

    /**
     * 페이징 결과가 limit보다 1개 많으면 초과된 요소를 제거하여 최대 크기를 보장합니다.
     *
     * @param unionTeams 팀 리스트
     * @param limit 최대 허용 크기
     * @return 최대 limit 크기의 리스트
     */
    public static <T> List<T> ensureMaxSize(List<T> unionTeams, Integer limit) {
        final boolean hasNext = unionTeams.size() > limit;
        if (hasNext) {
            log.info(
                    "TeamUnionView ensureMaxSize: hasNext true, limit={} → size={}",
                    limit,
                    unionTeams.size());
            return unionTeams.subList(0, limit);
        }
        log.info("TeamUnionView ensureMaxSize: hasNext false - size={}", unionTeams.size());
        return unionTeams;
    }
}
