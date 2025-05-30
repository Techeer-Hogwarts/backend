package backend.techeerzip.domain.studyTeam.controller;

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

import backend.techeerzip.domain.studyTeam.dto.request.StudyAddMembersRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyApplicantRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudySlackRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamApplyRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamCreateRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamUpdateRequest;
import backend.techeerzip.domain.studyTeam.dto.response.StudyApplicantResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamCreateResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamDetailResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamUpdateResponse;
import backend.techeerzip.domain.studyTeam.service.StudyTeamFacadeService;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.global.resolver.UserId;
import backend.techeerzip.infra.slack.SlackEvent;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v3/studyTeams")
@RequiredArgsConstructor
public class StudyTeamController implements StudyTeamSwagger {

    private final StudyTeamFacadeService studyTeamFacadeService;
    private final ApplicationEventPublisher eventPublisher;
    private final CustomLogger log;

    @GetMapping("/{studyTeamId}")
    public ResponseEntity<StudyTeamDetailResponse> getDetail(@PathVariable Long studyTeamId) {
        return ResponseEntity.ok(studyTeamFacadeService.getDetail(studyTeamId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createStudyTeam(
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,
            @RequestPart("createStudyTeamRequest") StudyTeamCreateRequest request) {
        final StudyTeamCreateResponse response =
                studyTeamFacadeService.create(resultImages, request);
        log.debug("StudyTeam Create: 완료");
        return ResponseEntity.ok(response.id());
    }

    @PatchMapping(value = "/{studyTeamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> updateStudyTeam(
            @PathVariable Long studyTeamId,
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,
            @RequestPart("updateStudyTeamRequest") StudyTeamUpdateRequest request,
            @UserId Long userId) {
        final StudyTeamUpdateResponse response =
                studyTeamFacadeService.update(studyTeamId, userId, resultImages, request);
        return ResponseEntity.ok(response.id());
    }

    @PatchMapping("/close/{studyTeamId}")
    public ResponseEntity<Void> closeRecruit(@PathVariable Long studyTeamId, @UserId Long userId) {
        studyTeamFacadeService.closeRecruit(studyTeamId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/delete/{studyTeamId}")
    public ResponseEntity<Void> deleteStudyTeam(
            @PathVariable Long studyTeamId, @UserId Long userId) {
        studyTeamFacadeService.softDeleteTeam(studyTeamId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{studyTeamId}/applicants")
    public ResponseEntity<List<StudyApplicantResponse>> getApplicants(
            @PathVariable Long studyTeamId, @UserId Long userId) {
        return ResponseEntity.ok(studyTeamFacadeService.getApplicants(studyTeamId, userId));
    }

    @PostMapping("/apply")
    public ResponseEntity<Void> applyToStudyTeam(
            @RequestBody StudyTeamApplyRequest request, @UserId Long userId) {
        List<StudySlackRequest.DM> slackRequest =
                studyTeamFacadeService.applyToStudy(request, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{studyTeamId}/cancel")
    public ResponseEntity<Void> cancelApplication(
            @PathVariable Long studyTeamId, @UserId Long userId) {
        List<StudySlackRequest.DM> slackRequest =
                studyTeamFacadeService.cancelApplication(studyTeamId, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/applicants/accept")
    public ResponseEntity<Void> acceptApplicant(
            @RequestBody StudyApplicantRequest request, @UserId Long userId) {
        List<StudySlackRequest.DM> slackRequest =
                studyTeamFacadeService.acceptApplicant(request, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/applicants/reject")
    public ResponseEntity<Void> rejectApplicant(
            @RequestBody StudyApplicantRequest request, @UserId Long userId) {
        List<StudySlackRequest.DM> slackRequest =
                studyTeamFacadeService.rejectApplicant(request, userId);
        eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/members")
    public ResponseEntity<Void> addStudyMembers(
            @RequestBody StudyAddMembersRequest request, @UserId Long userId) {
        studyTeamFacadeService.addMemberToStudyTeam(
                request.studyTeamId(), userId, request.studyMemberId(), request.isLeader());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
