package backend.techeerzip.domain.projectTeam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

import backend.techeerzip.domain.projectTeam.type.PositionType;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * 스터디 팀을 커서 기반으로 조회하기 위한 요청 DTO입니다. 정렬 기준에 따라 커서 필드를 사용하고, 모집 여부 필터링이 가능합니다.
 *
 * <p>서비스에서는 {@code sortType}에 따라 커서 기준 필드가 달라지며, {@code idCursor}, {@code dateCursor}, {@code
 * countCursor}가 그에 맞게 사용됩니다.
 *
 * <ul>
 *   <li>{@code sortType}이 {@code UPDATE_AT_DESC}인 경우 → {@code dateCursor}, {@code idCursor} 기준
 *   <li>{@code sortType}이 {@code VIEW_COUNT_DESC} 또는 {@code LIKE_COUNT_DESC}인 경우 → {@code
 *       countCursor}, {@code idCursor} 기준
 * </ul>
 *
 * <p>널일 경우의 동작:
 *
 * <ul>
 *   <li>{@code idCursor} → null이면 ID 비교 없이 단일 커서 필드로만 조회
 *   <li>{@code dateCursor}, {@code countCursor} → null이면 커서 조건 없이 최신부터 정렬
 *   <li>{@code isRecruited}, {@code isFinished} → null이면 모집 상태 조건 없이 전체 포함
 * </ul>
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "전체 팀(프로젝트/스터디) 커서 기반 조회 요청 DTO")
public class GetTeamsQueryRequest {

    @Schema(description = "커서용 팀 ID (정렬 보조 키)", example = "103")
    private Long id;

    @Schema(description = "커서용 날짜 (UPDATE_AT_DESC 정렬일 경우 사용)", example = "2025-05-01T18:30:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateCursor;

    @Schema(description = "커서용 조회수 또는 좋아요 수 (VIEW_COUNT_DESC, LIKE_COUNT_DESC 정렬일 경우 사용)", example = "250")
    private Integer countCursor;

    @Min(1)
    @Schema(description = "페이지당 조회할 팀 개수 (1 이상)", example = "10", defaultValue = "10")
    private Integer limit;

    @Schema(
            description = "정렬 기준 (UPDATE_AT_DESC, VIEW_COUNT_DESC, LIKE_COUNT_DESC)",
            example = "UPDATE_AT_DESC",
            defaultValue = "UPDATE_AT_DESC"
    )
    private SortType sortType;

    @Schema(
            description = "팀 유형 리스트 (project, study), 생략 시 전체",
            example = "[\"PROJECT\", \"STUDY\"]"
    )
    private List<TeamType> teamTypes;

    @Schema(
            description = "조회할 포지션 목록. 프로젝트 팀에만 적용됩니다.",
            example = "[\"FRONTEND\", \"BACKEND\", \"DEVOPS\", \"FULLSTACK\", \"DATA_ENGINEER\"]"
    )
    private List<PositionType> positions;

    @Schema(
            description = "모집중 필터 (true: 모집 중인 팀만, false: 마감된 팀만, null: 전체 포함)",
            example = "true"
    )
    private Boolean isRecruited;

    @Schema(
            description = "진행 상태 필터 (true: 완료된 팀, false: 진행 중인 팀, null: 전체 포함)",
            example = "false"
    )
    private Boolean isFinished;

    /**
     * 기본 정렬 방식이 없을 경우 UPDATE_AT_DESC로 설정한 복사 인스턴스를 반환합니다.
     */
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
