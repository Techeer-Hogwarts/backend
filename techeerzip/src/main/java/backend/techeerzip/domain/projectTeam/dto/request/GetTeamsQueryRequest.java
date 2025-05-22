package backend.techeerzip.domain.projectTeam.dto.request;

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
    private LocalDateTime createAtCursor;
    private Long limit;
    private List<TeamType> teamTypes;
    private List<PositionType> positions;
    private Boolean isRecruited;
    private Boolean isFinished;
}
