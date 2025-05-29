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

@Getter
@Builder
@AllArgsConstructor
public class GetTeamsQuery {
    private final Long id;
    private final LocalDateTime dateCursor;
    private final Integer countCursor;

    @Min(1)
    private final int limit;

    private final LocalDateTime createAt;

    @NotNull private final List<TeamType> teamTypes;
    private final SortType sortType;
    private final Boolean isRecruited;
    private final Boolean isFinished;
}
