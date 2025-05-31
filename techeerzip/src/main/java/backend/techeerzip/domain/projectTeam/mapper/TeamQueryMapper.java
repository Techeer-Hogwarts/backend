package backend.techeerzip.domain.projectTeam.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

import jakarta.validation.constraints.NotNull;

import backend.techeerzip.domain.projectTeam.dto.request.GetProjectTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetStudyTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectSliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.SliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.studyTeam.dto.response.StudySliceTeamsResponse;

/**
 * 팀 통합 조회 요청을 내부 도메인별 쿼리 객체(Project/Study)로 변환하거나,
 * 정렬 기준에 따라 페이징을 처리한 결과를 하나의 리스트로 병합하는 유틸리티 클래스입니다.
 * <p>
 * 주로 다음의 역할을 수행합니다:
 * <ul>
 *   <li>GetTeamsQueryRequest → GetTeamsQuery 로 변환</li>
 *   <li>팀 타입에 따라 GetProjectTeamsQuery, GetStudyTeamsQuery 분기 생성</li>
 *   <li>정렬 기준에 따라 date 또는 count 기반 커서 분기</li>
 *   <li>스터디/프로젝트 결과를 우선순위 큐로 정렬 및 통합</li>
 * </ul>
 *
 * <p>※ 이 클래스는 생성자가 private인 static 유틸 클래스로만 동작합니다.</p>
 */
public class TeamQueryMapper {
    private static final int DEFAULT_LIMIT = 10;

    private TeamQueryMapper() {}

    /**
     * 통합 요청 DTO(GetTeamsQueryRequest)를 내부 커서 기반 쿼리 객체(GetTeamsQuery)로 변환합니다.
     * 정렬 기준에 따라 날짜 기반 또는 수치 기반 쿼리로 분기됩니다.
     *
     * @param request 클라이언트 요청 DTO
     * @return 내부 공통 쿼리 객체
     */
    public static GetTeamsQuery mapToTeamsQuery(@NotNull GetTeamsQueryRequest request) {
        final SortType sortType = request.getSortType();
        return switch (sortType) {
            case UPDATE_AT_DESC -> mapWithDateCursor(sortType, request);
            case VIEW_COUNT_DESC, LIKE_COUNT_DESC -> mapWithCountCursor(sortType, request);
        };
    }

    /**
     * 날짜 기반 커서 페이징용 쿼리 객체를 생성합니다.
     * <p>※ dateCursor는 null인 경우 현재 시간으로 대체됩니다.</p>
     */
    private static GetTeamsQuery mapWithDateCursor(SortType sortType, GetTeamsQueryRequest req) {
        return GetTeamsQuery.builder()
                .id(req.getId())
                .dateCursor(Optional.ofNullable(req.getDateCursor()).orElse(LocalDateTime.now()))
                .countCursor(null)
                .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                .teamTypes(Optional.ofNullable(req.getTeamTypes()).orElse(List.of()))
                .sortType(sortType)
                .isRecruited(req.getIsRecruited())
                .isFinished(req.getIsFinished())
                .build();
    }

    /**
     * count 기반 커서 페이징용 쿼리 객체를 생성합니다.
     * <p>예: 조회수(viewCount), 좋아요 수(likeCount) 기반 정렬 시 사용됩니다.</p>
     */
    private static GetTeamsQuery mapWithCountCursor(SortType sortType, GetTeamsQueryRequest req) {
        return GetTeamsQuery.builder()
                .id(req.getId())
                .dateCursor(null)
                .countCursor(req.getCountCursor())
                .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                .teamTypes(Optional.ofNullable(req.getTeamTypes()).orElse(List.of()))
                .sortType(sortType)
                .isRecruited(req.getIsRecruited())
                .isFinished(req.getIsFinished())
                .build();
    }

    /**
     * 통합 요청 DTO를 프로젝트 팀 전용 쿼리로 변환합니다.
     * <p>
     * 정렬 기준에 따라 {@code dateCursor} 또는 {@code countCursor}를 세팅하며,
     * 포지션 필터(PositionNumType)도 함께 매핑합니다.
     * </p>
     */
    public static GetProjectTeamsQuery mapToProjectQuery(GetTeamsQueryRequest req) {
        final GetProjectTeamsQuery.GetProjectTeamsQueryBuilder builder =
                GetProjectTeamsQuery.builder()
                        .idCursor(req.getId())
                        .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                        .sortType(
                                Optional.ofNullable(req.getSortType())
                                        .orElse(SortType.UPDATE_AT_DESC))
                        .isRecruited(req.getIsRecruited())
                        .isFinished(req.getIsFinished())
                        .positionNumTypes(
                                PositionNumType.fromMany(
                                        Optional.ofNullable(req.getPositions()).orElse(List.of())));

        return switch (req.getSortType()) {
            case UPDATE_AT_DESC ->
                    builder.dateCursor(
                                    Optional.ofNullable(req.getDateCursor())
                                            .orElse(LocalDateTime.now()))
                            .build();
            case VIEW_COUNT_DESC, LIKE_COUNT_DESC ->
                    builder.countCursor(req.getCountCursor()).build();
        };
    }

    /**
     * 통합 요청 DTO를 스터디 팀 전용 쿼리로 변환합니다.
     * <p>포지션 타입 필터는 없으며, isRecruited / isFinished 필터링 조건만 포함됩니다.</p>
     */
    public static GetStudyTeamsQuery mapToStudyQuery(GetTeamsQueryRequest req) {
        final GetStudyTeamsQuery.GetStudyTeamsQueryBuilder builder =
                GetStudyTeamsQuery.builder()
                        .idCursor(req.getId())
                        .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                        .sortType(
                                Optional.ofNullable(req.getSortType())
                                        .orElse(SortType.UPDATE_AT_DESC))
                        .isRecruited(req.getIsRecruited())
                        .isFinished(req.getIsFinished());

        return switch (req.getSortType()) {
            case UPDATE_AT_DESC ->
                    builder.dateCursor(
                                    Optional.ofNullable(req.getDateCursor())
                                            .orElse(LocalDateTime.now()))
                            .build();
            case VIEW_COUNT_DESC, LIKE_COUNT_DESC ->
                    builder.countCursor(req.getCountCursor()).build();
        };
    }

    /**
     * 스터디와 프로젝트 팀 결과 리스트를 하나의 리스트로 정렬 통합합니다.
     * <p>
     * PriorityQueue를 사용해 동적 정렬되며, sortType에 따라 updatedAt, viewCount, likeCount를 기준으로 정렬됩니다.
     * </p>
     *
     * @param projectResponses 프로젝트 팀 응답 리스트
     * @param studyResponses 스터디 팀 응답 리스트
     * @param sortType 정렬 기준
     * @return 통합 정렬된 SliceTeamsResponse 리스트
     */
    public static List<SliceTeamsResponse> toTeamGetAllResponse(
            List<ProjectSliceTeamsResponse> projectResponses,
            List<StudySliceTeamsResponse> studyResponses,
            SortType sortType) {

        // 정렬 기준에 따른 Comparator 생성
        final Comparator<SliceTeamsResponse> comparator =
                switch (sortType) {
                    case UPDATE_AT_DESC -> Comparator.comparing(SliceTeamsResponse::getUpdatedAt);
                    case VIEW_COUNT_DESC -> Comparator.comparing(SliceTeamsResponse::getViewCount);
                    case LIKE_COUNT_DESC -> Comparator.comparing(SliceTeamsResponse::getLikeCount);
                };

        // 우선순위 큐를 사용해 정렬 결과 누적
        final PriorityQueue<SliceTeamsResponse> queue = new PriorityQueue<>(comparator.reversed());
        queue.addAll(projectResponses);
        queue.addAll(studyResponses);

        // poll 하여 정렬 결과 리스트로 변환
        final List<SliceTeamsResponse> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            result.add(queue.poll());
        }

        return result;
    }
}
