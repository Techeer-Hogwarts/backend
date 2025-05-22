package backend.techeerzip.domain.projectTeam.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetTeamsQuery {
    private final UUID globalId;
    private final LocalDateTime createAtCursor;

    @Min(1)
    private final Long limit;

    @NotNull private final List<TeamType> teamTypes;
    @NotNull private final List<PositionNumType> positionNumTypes;
    private final Boolean isRecruited;
    private final Boolean isFinished;
}
