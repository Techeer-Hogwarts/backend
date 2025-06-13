package backend.techeerzip.domain.projectTeam.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 프로젝트와 스터디 팀을 통합 조회하기 위한 요청 DTO입니다. 커서 기반 페이징, 정렬, 팀 타입 및 모집 상태 조건에 따라 결과를 필터링합니다.
 *
 * <p>서비스에서는 {@code sortType}에 따라 커서 기준 필드가 결정되며, {@code id}, {@code dateCursor}, {@code
 * countCursor}는 그에 따라 함께 사용됩니다.
 *
 * <ul>
 *   <li>{@code sortType}이 {@code UPDATE_AT_DESC}인 경우 → {@code dateCursor}, {@code id}가 커서 기준
 *   <li>{@code sortType}이 {@code VIEW_COUNT_DESC} 또는 {@code LIKE_COUNT_DESC}인 경우 → {@code
 *       countCursor}, {@code id}가 커서 기준
 * </ul>
 *
 * <p>널일 경우의 동작:
 *
 * <ul>
 *   <li>{@code id} → null이면 ID 비교 없이 단일 정렬 필드만으로 커서 처리
 *   <li>{@code dateCursor}, {@code countCursor} → null이면 커서 조건 없이 최신부터 정렬
 *   <li>{@code createAt} → null이면 생성일 필터링 조건 없이 모든 팀 포함
 *   <li>{@code isRecruited}, {@code isFinished} → null이면 필터링 조건 없이 전체 포함
 * </ul>
 */
@Getter
@Builder
@AllArgsConstructor
public class GetTeamsQuery {
    private final Long id;
    private final LocalDateTime dateCursor;
    private final Integer countCursor;

    @Min(1)
    private final int limit;

    @NotNull private final List<TeamType> teamTypes;
    private final SortType sortType;
    private final Boolean isRecruited;
    private final Boolean isFinished;
}
