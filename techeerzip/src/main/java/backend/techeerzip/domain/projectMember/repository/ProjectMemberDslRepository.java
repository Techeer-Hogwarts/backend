package backend.techeerzip.domain.projectMember.repository;

import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.response.ProjectMemberApplicantResponse;

public interface ProjectMemberDslRepository {

    List<ProjectMemberApplicantResponse> findManyApplicants(Long teamId);
}
