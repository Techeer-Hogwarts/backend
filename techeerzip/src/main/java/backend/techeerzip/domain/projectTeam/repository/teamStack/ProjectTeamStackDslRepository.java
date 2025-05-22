package backend.techeerzip.domain.projectTeam.repository.teamStack;

import java.util.List;

import backend.techeerzip.domain.projectTeam.entity.TeamStack;

public interface ProjectTeamStackDslRepository {

    List<TeamStack> findTeamStackByStackAndProjectTeamId(Long projectTeamId);
}
