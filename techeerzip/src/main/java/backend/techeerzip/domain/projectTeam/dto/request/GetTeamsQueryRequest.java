package backend.techeerzip.domain.projectTeam.dto.request;

import backend.techeerzip.domain.projectTeam.type.SortType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import backend.techeerzip.domain.projectTeam.type.PositionType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetTeamsQueryRequest {

    private UUID globalId;
    private Long id;
    private LocalDateTime createAtCursor;
    private Integer likeCountCursor;
    private Integer viewCountCursor;
    private Integer limit;
    private SortType sortType;
    private List<TeamType> teamTypes;
    private List<PositionType> positions;
    private Boolean isRecruited;
    private Boolean isFinished;
}
