package backend.techeerzip.domain.projectTeam.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectTeam.dto.request.EmptyResponse;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectApplicantRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamApplyRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.TeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.mapper.ProjectTeamMapper;
import backend.techeerzip.domain.projectTeam.service.ProjectTeamFacadeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projectTeams")
@RequiredArgsConstructor
public class ProjectTeamController implements ProjectTeamSwagger {

    private final ProjectTeamFacadeService projectTeamFacadeService;

    @GetMapping("/{projectTeamId}")
    public ResponseEntity<ProjectTeamDetailResponse> getDetail(@PathVariable Long projectTeamId) {
        return projectTeamFacadeService.getDetail(projectTeamId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createProjectTeam(
            @RequestPart("files") List<MultipartFile> images,
            @RequestPart("createProjectTeamRequest") ProjectTeamCreateRequest request) {
        return projectTeamFacadeService.create(ProjectTeamMapper.separateImages(images), request);
    }

    @PatchMapping(value = "/{projectTeamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> updateProjectTeam(
            @PathVariable Long projectTeamId,
            @RequestPart(value = "mainImages", required = false) MultipartFile mainImage,
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,
            @RequestPart("updateProjectTeamRequest") ProjectTeamUpdateRequest request) {
        final Long userId = 1L;
        return projectTeamFacadeService.update(
                projectTeamId, userId, mainImage, resultImages, request);
    }

    @GetMapping("/allTeams")
    public ResponseEntity<List<TeamGetAllResponse>> getAllTeams(GetTeamsQueryRequest request) {
        return projectTeamFacadeService.getAllProjectAndStudyTeams(request);
    }

    @PatchMapping("/close/{projectTeamId}")
    public ResponseEntity<EmptyResponse> closeRecruit(@PathVariable Long projectTeamId) {
        final Long userId = 1L;
        return projectTeamFacadeService.closeRecruit(projectTeamId, userId);
    }

    @PatchMapping("/delete/{projectTeamId}")
    public ResponseEntity<EmptyResponse> deleteProjectTeam(@PathVariable Long projectTeamId) {
        final Long userId = 1L;
        return projectTeamFacadeService.softDeleteTeam(projectTeamId, userId);
    }

    @PostMapping("/apply")
    public ResponseEntity<EmptyResponse> applyToProject(ProjectTeamApplyRequest request) {
        final Long userId = 1L;
        return projectTeamFacadeService.applyToProject(request, userId);
    }

    @GetMapping("/{projectTeamId}/applicants")
    public ResponseEntity<List<ProjectMemberApplicantResponse>> getApplicants(
            @PathVariable Long projectTeamId) {
        final Long userId = 1L;
        return projectTeamFacadeService.getApplicants(projectTeamId, userId);
    }

    @PatchMapping("/{projectTeamId}/cancel")
    public ResponseEntity<EmptyResponse> cancelApplication(@PathVariable Long projectTeamId) {
        final Long userId = 1L;
        return projectTeamFacadeService.cancelApplication(projectTeamId, userId);
    }

    @PatchMapping("/accept")
    public ResponseEntity<EmptyResponse> acceptApplicant(ProjectApplicantRequest request) {
        final Long userId = 1L;
        return projectTeamFacadeService.acceptApplicant(request, userId);
    }

    @PatchMapping("/reject")
    public ResponseEntity<EmptyResponse> rejectApplicant(ProjectApplicantRequest request) {
        final Long userId = 1L;
        return projectTeamFacadeService.rejectApplicant(request, userId);
    }
}
