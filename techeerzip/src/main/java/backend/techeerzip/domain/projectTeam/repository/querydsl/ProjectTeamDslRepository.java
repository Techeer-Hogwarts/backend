package backend.techeerzip.domain.projectTeam.repository.querydsl;

import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.dto.response.TeamLeaderAlertData;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;

public interface ProjectTeamDslRepository {

    List<ProjectTeamGetAllResponse> sliceYoungTeam(
            List<PositionNumType> positionNumType,
            Boolean isRecruited,
            Boolean isFinished,
            Long limit);

    List<ProjectTeamGetAllResponse> findManyYoungTeamById(
            List<Long> keys, Boolean isRecruited, Boolean isFinished);

    List<TeamLeaderAlertData> findAlertDataForLeader(Long teamId);

    //    List<ProjectUserTeamsResponse> findAllTeamsByUserId(Long userId);
}
