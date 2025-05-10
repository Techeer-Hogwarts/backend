package backend.techeerzip.domain.projectTeam.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamGetAllListResponse {

    private final List<TeamGetAllResponse> allTeams;
}
