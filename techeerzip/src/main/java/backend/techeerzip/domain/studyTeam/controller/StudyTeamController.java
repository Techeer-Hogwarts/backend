package backend.techeerzip.domain.studyTeam.controller;

import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.domain.studyTeam.dto.request.StudySlackRequest.DM;
import backend.techeerzip.infra.index.IndexEvent;
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
        log.info("StudyTeam getDetail: 상세 조회 시작 - teamId={}", studyTeamId);
        final StudyTeamDetailResponse response = studyTeamFacadeService.getDetail(studyTeamId);
        log.info("StudyTeam getDetail: 조회 완료 - teamId={}", studyTeamId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createStudyTeam(
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,
            @RequestPart("createStudyTeamRequest") StudyTeamCreateRequest request) {
        log.info("StudyTeam createStudyTeam: 생성 요청 시작");

        final StudyTeamCreateResponse response =
                studyTeamFacadeService.create(resultImages, request);
        log.info("StudyTeam createStudyTeam: 생성 완료 - teamId={}", response.id());

        eventPublisher.publishEvent(new SlackEvent.Channel<>(response.slackRequest()));
        log.info("StudyTeam createStudyTeam: Slack 이벤트 전송 완료");

        eventPublisher.publishEvent(
                new IndexEvent.Create<>(
                        TeamType.STUDY.getLow(), response.indexRequest()));
        log.info("StudyTeam createStudyTeam: Index 이벤트 전송 완료");
        return ResponseEntity.ok(response.id());
    }

    @PatchMapping(value = "/{studyTeamId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> updateStudyTeam(
            @PathVariable Long studyTeamId,
            @RequestPart(value = "resultImages", required = false) List<MultipartFile> resultImages,
            @RequestPart("updateStudyTeamRequest") StudyTeamUpdateRequest request,
            @UserId Long userId) {
        log.info("StudyTeam updateStudyTeam: 수정 요청 시작 - teamId={}, userId={}", studyTeamId, userId);

        final StudyTeamUpdateResponse response =
                studyTeamFacadeService.update(studyTeamId, userId, resultImages, request);
        log.info("StudyTeam updateStudyTeam: 수정 완료 - teamId={}", response.id());

        if (response.slackRequest() != null) {
            eventPublisher.publishEvent(new SlackEvent.Channel<>(response.slackRequest()));
            log.info("ProjectTeam updateStudyTeam: Slack 이벤트 전송 완료");
        }

        eventPublisher.publishEvent(
                new IndexEvent.Create<>(
                        response.indexRequest().getName(), response.indexRequest()));
        log.info("StudyTeam updateStudyTeam: Index 이벤트 전송 완료");
        return ResponseEntity.ok(response.id());
    }

    @PatchMapping("/close/{studyTeamId}")
    public ResponseEntity<Void> closeRecruit(@PathVariable Long studyTeamId, @UserId Long userId) {
        log.info("StudyTeam closeRecruit: 모집 마감 요청 - teamId={}, userId={}", studyTeamId, userId);
        studyTeamFacadeService.closeRecruit(studyTeamId, userId);
        log.info("StudyTeam closeRecruit: 모집 마감 완료 - teamId={}", studyTeamId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/delete/{studyTeamId}")
    public ResponseEntity<Void> deleteStudyTeam(
            @PathVariable Long studyTeamId, @UserId Long userId) {
        log.info("StudyTeam deleteStudyTeam: 삭제 요청 - teamId={}, userId={}", studyTeamId, userId);
        studyTeamFacadeService.softDeleteTeam(studyTeamId, userId);
        log.info("StudyTeam deleteStudyTeam: 삭제 완료 - teamId={}", studyTeamId);
        eventPublisher.publishEvent(
                new IndexEvent.Delete(
                        TeamType.STUDY.getLow(), studyTeamId));
        log.info("ProjectTeam deleteStudyTeam: 삭제 완료 - teamId={}", studyTeamId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{studyTeamId}/applicants")
    public ResponseEntity<List<StudyApplicantResponse>> getApplicants(
            @PathVariable Long studyTeamId, @UserId Long userId) {
        log.info("StudyTeam getApplicants: 지원자 조회 요청 - teamId={}, userId={}", studyTeamId, userId);
        final List<StudyApplicantResponse> applicants =
                studyTeamFacadeService.getApplicants(studyTeamId, userId);
        log.info("StudyTeam getApplicants: 조회 완료 - 지원자 수={}", applicants.size());
        return ResponseEntity.ok(applicants);
    }

    @PostMapping("/apply")
    public ResponseEntity<Void> applyToStudyTeam(
            @RequestBody StudyTeamApplyRequest request, @UserId Long userId) {
        log.info("StudyTeam applyToStudyTeam: 지원 요청 시작 - userId={}", userId);
        List<StudySlackRequest.DM> slackRequests =
                studyTeamFacadeService.applyToStudy(request, userId);
        for (DM slackRequest : slackRequests) {
            eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        }         log.info("StudyTeam applyToStudyTeam: Slack 이벤트 전송 완료");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{studyTeamId}/cancel")
    public ResponseEntity<Void> cancelApplication(
            @PathVariable Long studyTeamId, @UserId Long userId) {
        log.info(
                "StudyTeam cancelApplication: 지원 취소 요청 - teamId={}, userId={}",
                studyTeamId,
                userId);
        List<StudySlackRequest.DM> slackRequests =
                studyTeamFacadeService.cancelApplication(studyTeamId, userId);
        for (DM slackRequest : slackRequests) {
            eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        }         log.info("StudyTeam cancelApplication: Slack 이벤트 전송 완료");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/applicants/accept")
    public ResponseEntity<Void> acceptApplicant(
            @RequestBody StudyApplicantRequest request, @UserId Long userId) {
        log.info("StudyTeam acceptApplicant: 지원자 수락 요청 - userId={}", userId);
        List<StudySlackRequest.DM> slackRequests =
                studyTeamFacadeService.acceptApplicant(request, userId);
        for (DM slackRequest : slackRequests) {
            eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        }
        log.info("StudyTeam acceptApplicant: Slack 이벤트 전송 완료");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/applicants/reject")
    public ResponseEntity<Void> rejectApplicant(
            @RequestBody StudyApplicantRequest request, @UserId Long userId) {
        log.info("StudyTeam rejectApplicant: 지원자 거절 요청 - userId={}", userId);
        final List<StudySlackRequest.DM> slackRequests =
                studyTeamFacadeService.rejectApplicant(request, userId);
        for (DM slackRequest : slackRequests) {
            eventPublisher.publishEvent(new SlackEvent.DM<>(slackRequest));
        }
        log.info("StudyTeam rejectApplicant: Slack 이벤트 전송 완료");
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/members")
    public ResponseEntity<Void> addStudyMembers(
            @RequestBody StudyAddMembersRequest request, @UserId Long userId) {
        log.info(
                "StudyTeam addStudyMembers: 멤버 추가 요청 - teamId={}, newMemberId={}, leader={}",
                request.studyTeamId(),
                request.studyMemberId(),
                request.isLeader());
        studyTeamFacadeService.addMemberToStudyTeam(
                request.studyTeamId(), userId, request.studyMemberId(), request.isLeader());
        log.info("StudyTeam addStudyMembers: 멤버 추가 완료 - teamId={}", request.studyTeamId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
