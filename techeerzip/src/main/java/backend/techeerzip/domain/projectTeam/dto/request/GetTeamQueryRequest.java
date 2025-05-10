package backend.techeerzip.domain.projectTeam.dto.request;

import java.util.List;

import jakarta.annotation.Nullable;

import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTeamQueryRequest {

    @Nullable private List<TeamType> teamTypes;
    @Nullable private List<PositionNumType> positionNumType;
    @Nullable private Boolean isRecruited;
    @Nullable private Boolean isFinished;
}
