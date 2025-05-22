package backend.techeerzip.domain.projectTeam.dto.request;

import backend.techeerzip.domain.projectTeam.type.TeamRole;

public record ProjectTeamApplyRequest(Long projectTeamId, TeamRole teamRole, String summary) {}
