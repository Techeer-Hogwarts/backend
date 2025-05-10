package backend.techeerzip.domain.projectTeam.repository.querydsl;

import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;

public interface ProjectTeamDslRepository {

    List<ProjectTeamGetAllResponse> fetchProjectTeams(
            List<PositionNumType> positionNumType, Boolean isRecruited, Boolean isFinished);
}
