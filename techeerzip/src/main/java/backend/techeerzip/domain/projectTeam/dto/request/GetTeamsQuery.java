package backend.techeerzip.domain.projectTeam.dto.request;

import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetTeamsQuery {
    private final UUID globalId;
    private final LocalDateTime createAtCursor;
    private final Long limit;
    private final List<TeamType> teamTypes;
    private final List<PositionNumType> positionNumTypes;
    private final Boolean isRecruited;
    private final Boolean isFinished;
}
