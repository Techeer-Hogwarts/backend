package backend.techeerzip.domain.projectTeam.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import backend.techeerzip.domain.projectTeam.type.PositionType;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 스터디 팀을 커서 기반으로 조회하기 위한 요청 DTO입니다.
 * 정렬 기준에 따라 커서 필드를 사용하고, 모집 여부 필터링이 가능합니다.
 *
 * <p>서비스에서는 {@code sortType}에 따라 커서 기준 필드가 달라지며,
 * {@code idCursor}, {@code dateCursor}, {@code countCursor}가 그에 맞게 사용됩니다.</p>
 *
 * <ul>
 *   <li>{@code sortType}이 {@code UPDATE_AT_DESC}인 경우 → {@code dateCursor}, {@code idCursor} 기준</li>
 *   <li>{@code sortType}이 {@code VIEW_COUNT_DESC} 또는 {@code LIKE_COUNT_DESC}인 경우 → {@code countCursor}, {@code idCursor} 기준</li>
 * </ul>
 *
 * <p>널일 경우의 동작:</p>
 * <ul>
 *   <li>{@code idCursor} → null이면 ID 비교 없이 단일 커서 필드로만 조회</li>
 *   <li>{@code dateCursor}, {@code countCursor} → null이면 커서 조건 없이 최신부터 정렬</li>
 *   <li>{@code isRecruited}, {@code isFinished} → null이면 모집 상태 조건 없이 전체 포함</li>
 * </ul>
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTeamsQueryRequest {

    private Long id;
    private LocalDateTime dateCursor;
    private Integer countCursor;
    private Integer limit;
    private SortType sortType;
    private List<TeamType> teamTypes;
    private List<PositionType> positions;
    private Boolean isRecruited;
    private Boolean isFinished;

    public GetTeamsQueryRequest withDefaultSortType() {
        if (this.sortType != null) {
            return this;
        }

        return GetTeamsQueryRequest.builder()
                .id(this.id)
                .dateCursor(this.dateCursor)
                .countCursor(this.countCursor)
                .limit(this.limit)
                .sortType(SortType.UPDATE_AT_DESC)
                .teamTypes(this.teamTypes)
                .positions(this.positions)
                .isRecruited(this.isRecruited)
                .isFinished(this.isFinished)
                .build();
    }
}
