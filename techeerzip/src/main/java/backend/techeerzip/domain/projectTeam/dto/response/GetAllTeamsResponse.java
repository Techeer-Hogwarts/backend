package backend.techeerzip.domain.projectTeam.dto.response;

import java.util.List;

public record GetAllTeamsResponse(List<SliceTeamsResponse> teams, SliceNextCursor nextInfo) {}
