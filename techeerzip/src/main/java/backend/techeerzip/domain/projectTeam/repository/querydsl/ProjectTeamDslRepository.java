package backend.techeerzip.domain.projectTeam.repository.querydsl;

import backend.techeerzip.domain.projectTeam.dto.request.GetProjectTeamsQuery;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.response.ProjectSliceTeamsResponse;

public interface ProjectTeamDslRepository {

    List<ProjectSliceTeamsResponse> findManyYoungTeamById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished);

    List<ProjectTeam> sliceYoungTeams(GetProjectTeamsQuery query);
    List<ProjectTeam> sliceYoungTeamByDate(GetProjectTeamsQuery query);

    List<ProjectTeam> sliceYoungTeamByCount(GetProjectTeamsQuery query);

    //    List<ProjectUserTeamsResponse> findAllTeamsByUserId(Long userId);
}
