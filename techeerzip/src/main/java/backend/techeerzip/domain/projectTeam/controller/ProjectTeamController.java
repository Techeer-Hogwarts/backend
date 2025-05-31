package backend.techeerzip.domain.projectTeam.controller;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectTeam.dto.request.EmptyResponse;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectApplicantRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectSlackRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamApplyRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.response.GetAllTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamCreateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamUpdateResponse;
import backend.techeerzip.domain.projectTeam.service.ProjectTeamFacadeService;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.resolver.UserId;
import backend.techeerzip.infra.index.IndexEvent;
import backend.techeerzip.infra.slack.SlackEvent;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/projectTeams")
@RequiredArgsConstructor
public class ProjectTeamController implements ProjectTeamSwagger {

    private final ProjectTeamFacadeService projectTeamFacadeService;
    private final ApplicationEventPublisher eventPublisher;
    private final CustomLogger log;

    @GetMapping("/{projectTeamId}")
    public ResponseEntity<ProjectTeamDetailResponse> getDetail(@PathVariable Long projectTeamId) {
        return ResponseEntity.ok(projectTeamFacadeService.getDetail(projectTeamId));
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createProjectTeam(
            @RequestPart(value = "mainImage") MultipartFile mainImage,
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,
            @RequestPart("createProjectTeamRequest") ProjectTeamCreateRequest request) {
        ProjectTeamCreateResponse response =
                projectTeamFacadeService.create(mainImage, resultImages, request);

        // Slack Service
        eventPublisher.publishEvent(new SlackEvent.DM<>(response.slackRequest()));
        // Index Service
        eventPublisher.publishEvent(
                new IndexEvent.Create<>(
                        response.indexRequest().getName(), response.indexRequest()));
        log.debug("ProjectTeam Create: 이벤트 전송 완료");
        return ResponseEntity.ok(response.id());
    }

    @PatchMapping(value = "/{projectTeamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> updateProjectTeam(
            @PathVariable Long projectTeamId,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,
            @RequestPart("updateProjectTeamRequest") ProjectTeamUpdateRequest request,
            @UserId Long userId) {
        final ProjectTeamUpdateResponse response =
                projectTeamFacadeService.update(
                        projectTeamId, userId, mainImage, resultImages, request);
        if (response.slackRequest() != null) {
            eventPublisher.publishEvent(new SlackEvent.DM<>(response.slackRequest()));
        }
        eventPublisher.publishEvent(
                new IndexEvent.Create<>(
                        response.indexRequest().getName(), response.indexRequest()));
        return ResponseEntity.ok(response.id());
    }

    @PostMapping("/allTeams")
    public ResponseEntity<GetAllTeamsResponse> getAllTeams(
            @RequestBody GetTeamsQueryRequest request) {
        return ResponseEntity.ok(projectTeamFacadeService.getAllProjectAndStudyTeams(request));
    }

    @PatchMapping("/close/{projectTeamId}")
    public ResponseEntity<Void> closeRecruit(
            @PathVariable Long projectTeamId, @UserId Long userId) {
        projectTeamFacadeService.closeRecruit(projectTeamId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/delete/{projectTeamId}")
    public ResponseEntity<Void> deleteProjectTeam(
            @PathVariable Long projectTeamId, @UserId Long userId) {
        projectTeamFacadeService.softDeleteTeam(projectTeamId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{projectTeamId}/applicants")
    public ResponseEntity<List<ProjectMemberApplicantResponse>> getApplicants(
            @PathVariable Long projectTeamId, @UserId Long userId) {
        return ResponseEntity.ok(projectTeamFacadeService.getApplicants(projectTeamId, userId));
    }

    @PostMapping("/apply")
    public ResponseEntity<Void> applyToProject(
            @RequestBody ProjectTeamApplyRequest request, @UserId Long userId) {
        final List<ProjectSlackRequest.DM> slackRequest =
                projectTeamFacadeService.applyToProject(request, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{projectTeamId}/cancel")
    public ResponseEntity<EmptyResponse> cancelApplication(
            @PathVariable Long projectTeamId, @UserId Long userId) {
        final List<ProjectSlackRequest.DM> slackRequest =
                projectTeamFacadeService.cancelApplication(projectTeamId, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/accept")
    public ResponseEntity<EmptyResponse> acceptApplicant(
            @RequestBody ProjectApplicantRequest request, @UserId Long userId) {
        final List<ProjectSlackRequest.DM> slackRequest =
                projectTeamFacadeService.acceptApplicant(request, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/reject")
    public ResponseEntity<EmptyResponse> rejectApplicant(
            @RequestBody ProjectApplicantRequest request, @UserId Long userId) {
        final List<ProjectSlackRequest.DM> slackRequest =
                projectTeamFacadeService.rejectApplicant(request, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
