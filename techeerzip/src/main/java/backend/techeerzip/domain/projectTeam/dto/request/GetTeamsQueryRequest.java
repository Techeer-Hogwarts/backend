package backend.techeerzip.domain.projectTeam.dto.request;

import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import backend.techeerzip.domain.projectTeam.type.PositionType;
import backend.techeerzip.domain.projectTeam.type.SortType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
