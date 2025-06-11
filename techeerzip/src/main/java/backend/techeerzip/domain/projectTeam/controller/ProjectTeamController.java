package backend.techeerzip.domain.projectTeam.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberApplicantResponse;
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
        log.info("ProjectTeam getDetail: 상세 정보 조회 시작 - teamId={}", projectTeamId);
        final ProjectTeamDetailResponse response =
                projectTeamFacadeService.getDetail(projectTeamId);
        log.info("ProjectTeam getDetail: 조회 완료 - teamId={}", projectTeamId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createProjectTeam(
            @RequestPart(value = "mainImage") MultipartFile mainImage,
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,
            @Valid @RequestPart("createProjectTeamRequest") ProjectTeamCreateRequest request) {
        log.info("ProjectTeam createProjectTeam: 생성 요청 시작");
        ProjectTeamCreateResponse response =
                projectTeamFacadeService.create(mainImage, resultImages, request);
        log.info("ProjectTeam createProjectTeam: 생성 완료 - teamId={}", response.id());

        eventPublisher.publishEvent(new SlackEvent.DM<>(response.slackRequest()));
        log.info("ProjectTeam createProjectTeam: Slack 이벤트 전송 완료");

        eventPublisher.publishEvent(
                new IndexEvent.Create<>(
                        response.indexRequest().getName(), response.indexRequest()));
        log.info("ProjectTeam createProjectTeam: Index 이벤트 전송 완료");

        return ResponseEntity.ok(response.id());
    }

    @PatchMapping(value = "/{projectTeamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> updateProjectTeam(
            @PathVariable Long projectTeamId,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,
            @RequestPart("updateProjectTeamRequest") ProjectTeamUpdateRequest request,
            @UserId Long userId) {
        log.info("ProjectTeam updateProjectTeam: 수정 요청 시작 - teamId={}", projectTeamId);
        final ProjectTeamUpdateResponse response =
                projectTeamFacadeService.update(
                        projectTeamId, userId, mainImage, resultImages, request);
        log.info("ProjectTeam updateProjectTeam: 수정 완료 - teamId={}", projectTeamId);

        if (response.slackRequest() != null) {
            eventPublisher.publishEvent(new SlackEvent.DM<>(response.slackRequest()));
            log.info("ProjectTeam updateProjectTeam: Slack 이벤트 전송 완료");
        }

        eventPublisher.publishEvent(
                new IndexEvent.Create<>(
                        response.indexRequest().getName(), response.indexRequest()));
        log.info("ProjectTeam updateProjectTeam: Index 이벤트 전송 완료");

        return ResponseEntity.ok(response.id());
    }

    @GetMapping("/allTeams")
    public ResponseEntity<GetAllTeamsResponse> getAllTeams(
            @ModelAttribute @Valid GetTeamsQueryRequest request) {
        log.info("ProjectTeam getAllTeams: 전체 팀 목록 조회 시작 - request={}", request);
        final GetAllTeamsResponse response =
                projectTeamFacadeService.getAllProjectAndStudyTeams(request);
        log.info("ProjectTeam getAllTeams: 조회 완료 - resultCount={}", response.teams().size());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/close/{projectTeamId}")
    public ResponseEntity<Void> closeRecruit(
            @PathVariable Long projectTeamId, @UserId Long userId) {
        log.info(
                "ProjectTeam closeRecruit: 모집 마감 요청 - teamId={}, userId={}", projectTeamId, userId);
        projectTeamFacadeService.closeRecruit(projectTeamId, userId);
        log.info("ProjectTeam closeRecruit: 모집 마감 완료 - teamId={}", projectTeamId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/delete/{projectTeamId}")
    public ResponseEntity<Void> deleteProjectTeam(
            @PathVariable Long projectTeamId, @UserId Long userId) {
        log.info(
                "ProjectTeam deleteProjectTeam: 삭제 요청 - teamId={}, userId={}",
                projectTeamId,
                userId);
        projectTeamFacadeService.softDeleteTeam(projectTeamId, userId);
        log.info("ProjectTeam deleteProjectTeam: 삭제 완료 - teamId={}", projectTeamId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{projectTeamId}/applicants")
    public ResponseEntity<List<ProjectMemberApplicantResponse>> getApplicants(
            @PathVariable Long projectTeamId, @UserId Long userId) {
        log.info(
                "ProjectTeam getApplicants: 지원자 목록 조회 - teamId={}, userId={}",
                projectTeamId,
                userId);
        final List<ProjectMemberApplicantResponse> applicants =
                projectTeamFacadeService.getApplicants(projectTeamId, userId);
        log.info("ProjectTeam getApplicants: 조회 완료 - 지원자 수={}", applicants.size());
        return ResponseEntity.ok(applicants);
    }

    @PostMapping("/apply")
    public ResponseEntity<Void> applyToProject(
            @RequestBody ProjectTeamApplyRequest request, @UserId Long userId) {
        log.info("ProjectTeam applyToProject: 지원 요청 시작 - userId={}", userId);
        final List<ProjectSlackRequest.DM> slackRequest =
                projectTeamFacadeService.applyToProject(request, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        log.info("ProjectTeam applyToProject: Slack 이벤트 전송 완료");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{projectTeamId}/cancel")
    public ResponseEntity<Void> cancelApplication(
            @PathVariable Long projectTeamId, @UserId Long userId) {
        log.info(
                "ProjectTeam cancelApplication: 지원 취소 요청 - teamId={}, userId={}",
                projectTeamId,
                userId);
        final List<ProjectSlackRequest.DM> slackRequest =
                projectTeamFacadeService.cancelApplication(projectTeamId, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        log.info("ProjectTeam cancelApplication: Slack 이벤트 전송 완료");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/accept")
    public ResponseEntity<Void> acceptApplicant(
            @RequestBody ProjectApplicantRequest request, @UserId Long userId) {
        log.info("ProjectTeam acceptApplicant: 지원자 수락 요청 - userId={}", userId);
        final List<ProjectSlackRequest.DM> slackRequest =
                projectTeamFacadeService.acceptApplicant(request, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        log.info("ProjectTeam acceptApplicant: Slack 이벤트 전송 완료");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/reject")
    public ResponseEntity<Void> rejectApplicant(
            @RequestBody ProjectApplicantRequest request, @UserId Long userId) {
        log.info("ProjectTeam rejectApplicant: 지원자 거절 요청 - userId={}", userId);
        final List<ProjectSlackRequest.DM> slackRequest =
                projectTeamFacadeService.rejectApplicant(request, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        log.info("ProjectTeam rejectApplicant: Slack 이벤트 전송 완료");
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
