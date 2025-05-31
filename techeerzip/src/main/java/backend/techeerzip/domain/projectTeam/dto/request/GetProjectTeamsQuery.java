package backend.techeerzip.domain.projectTeam.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;

import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.SortType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 프로젝트 팀을 커서 기반으로 조회하기 위한 요청 DTO입니다. 정렬 기준에 따라 커서 필드를 함께 사용하며, 포지션 조건과 모집 상태로 필터링 가능합니다.
 *
 * <p>서비스에서는 {@code sortType}에 따라 커서 기준 필드가 달라지며, {@code idCursor}, {@code dateCursor}, {@code
 * countCursor}가 해당 정렬에 맞춰 사용됩니다.
 *
 * <ul>
 *   <li>{@code sortType}이 {@code UPDATE_AT_DESC}인 경우 → {@code dateCursor}, {@code idCursor}를 커서
 *       기준으로 사용
 *   <li>{@code sortType}이 {@code VIEW_COUNT_DESC} 또는 {@code LIKE_COUNT_DESC}인 경우 → {@code
 *       countCursor}, {@code idCursor}를 커서 기준으로 사용
 * </ul>
 *
 * <p>널일 경우의 동작:
 *
 * <ul>
 *   <li>{@code idCursor} → null이면 ID 비교 없이 단일 커서 필드만으로 조회
 *   <li>{@code dateCursor}, {@code countCursor} → null이면 커서 조건 없이 최신부터 정렬
 *   <li>{@code positionNumTypes} → null 또는 빈 리스트이면 포지션 조건 없이 전체 포함
 *   <li>{@code isRecruited}, {@code isFinished} → null이면 모집 상태 조건 없이 전체 포함
 * </ul>
 */
@Getter
@Builder
@AllArgsConstructor
@Schema(description = "프로젝트 팀 커서 기반 조회 요청 DTO")
public class GetProjectTeamsQuery {
    private final Long idCursor;
    private final LocalDateTime dateCursor;
    private final Integer countCursor;

    @Min(1)
    private final int limit;

    private final SortType sortType;
    private final List<PositionNumType> positionNumTypes;
    private final Boolean isRecruited;
    private final Boolean isFinished;
}
