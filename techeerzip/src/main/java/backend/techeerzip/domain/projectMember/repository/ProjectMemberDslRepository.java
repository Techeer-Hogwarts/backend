package backend.techeerzip.domain.projectMember.repository;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberApplicantResponse;
import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;

public interface ProjectMemberDslRepository {

    List<ProjectMemberApplicantResponse> findManyApplicants(Long teamId);

    List<LeaderInfo> findManyLeaders(Long teamId);
}
