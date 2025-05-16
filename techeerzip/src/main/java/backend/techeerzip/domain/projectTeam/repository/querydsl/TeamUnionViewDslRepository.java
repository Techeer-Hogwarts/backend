package backend.techeerzip.domain.projectTeam.repository.querydsl;

import backend.techeerzip.domain.projectTeam.dto.request.GetTeamQueryRequest.GetTeamQuery;
import backend.techeerzip.domain.projectTeam.dto.response.TeamUnionSliceYoungInfo;

public interface TeamUnionViewDslRepository {

    TeamUnionSliceYoungInfo fetchSliceBeforeCreatedAtDescCursor(GetTeamQuery query);
}
