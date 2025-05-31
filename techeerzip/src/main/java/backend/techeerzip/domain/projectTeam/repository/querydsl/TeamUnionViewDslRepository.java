package backend.techeerzip.domain.projectTeam.repository.querydsl;

import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.response.TeamUnionSliceResult;

public interface TeamUnionViewDslRepository {

    TeamUnionSliceResult fetchSliceTeams(GetTeamsQuery query);
}
