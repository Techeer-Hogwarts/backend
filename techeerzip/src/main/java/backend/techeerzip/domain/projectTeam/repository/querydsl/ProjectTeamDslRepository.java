package backend.techeerzip.domain.projectTeam.repository.querydsl;

import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.request.GetProjectTeamsQuery;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;

public interface ProjectTeamDslRepository {

    List<ProjectTeam> sliceTeams(GetProjectTeamsQuery query);

    List<ProjectTeam> sliceTeamsByDate(GetProjectTeamsQuery query);

    List<ProjectTeam> sliceTeamsByCount(GetProjectTeamsQuery query);

    //    List<ProjectUserTeamsResponse> findAllTeamsByUserId(Long userId);
}
