package backend.techeerzip.domain.projectTeam.service;

import backend.techeerzip.domain.projectTeam.dto.request.SlackRequest.DM;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamUpdateResponse;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectApplicantRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamApplyRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamCreateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.TeamGetAllResponse;

public interface ProjectTeamFacadeService {

    ProjectTeamDetailResponse getDetail(Long projectTeamId);

    ProjectTeamCreateResponse create(
            MultipartFile mainImage,
            List<MultipartFile> resultImages,
            ProjectTeamCreateRequest request);

    ProjectTeamUpdateResponse update(
            Long projectTeamId,
            Long userId,
            MultipartFile mainImage,
            List<MultipartFile> resultImages,
            ProjectTeamUpdateRequest request);

    List<TeamGetAllResponse> getAllProjectAndStudyTeams(
            GetTeamsQueryRequest request);

    void closeRecruit(Long projectTeamId, Long userId);

    void softDeleteTeam(Long projectTeamId, Long userId);

    List<DM> applyToProject(ProjectTeamApplyRequest request, Long userId);

    List<ProjectMemberApplicantResponse> getApplicants(Long teamId, Long userId);

    List<DM> cancelApplication(Long teamId, Long applicantId);

    List<DM> acceptApplicant(ProjectApplicantRequest request, Long userId);

    List<DM> rejectApplicant(ProjectApplicantRequest request, Long userId);
}
