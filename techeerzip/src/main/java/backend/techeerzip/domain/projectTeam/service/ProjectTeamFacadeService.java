package backend.techeerzip.domain.projectTeam.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectTeam.dto.request.EmptyResponse;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ImageRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectApplicantRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamApplyRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.TeamGetAllResponse;

public interface ProjectTeamFacadeService {

    ResponseEntity<ProjectTeamDetailResponse> getDetail(Long projectTeamId);

    ResponseEntity<Long> create(ImageRequest imageRequest, ProjectTeamCreateRequest request);

    ResponseEntity<Long> update(
            Long projectTeamId,
            Long userId,
            MultipartFile mainImage,
            List<MultipartFile> resultImages,
            ProjectTeamUpdateRequest request);

    ResponseEntity<List<TeamGetAllResponse>> getAllProjectAndStudyTeams(
            GetTeamsQueryRequest request);

    ResponseEntity<EmptyResponse> closeRecruit(Long projectTeamId, Long userId);

    ResponseEntity<EmptyResponse> softDeleteTeam(Long projectTeamId, Long userId);

    ResponseEntity<EmptyResponse> applyToProject(ProjectTeamApplyRequest request, Long userId);

    ResponseEntity<List<ProjectMemberApplicantResponse>> getApplicants(Long teamId, Long userId);

    ResponseEntity<EmptyResponse> cancelApplication(Long teamId, Long applicantId);

    ResponseEntity<EmptyResponse> acceptApplicant(ProjectApplicantRequest request, Long userId);

    ResponseEntity<EmptyResponse> rejectApplicant(ProjectApplicantRequest request, Long userId);
}
