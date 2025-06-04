package backend.techeerzip.domain.studyTeam.service;

import static backend.techeerzip.domain.projectTeam.service.ProjectTeamFacadeServiceImpl.validateResultImages;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.studyTeam.dto.request.StudyApplicantRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudySlackRequest.DM;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamApplyRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamCreateRequest;
import backend.techeerzip.domain.studyTeam.dto.request.StudyTeamUpdateRequest;
import backend.techeerzip.domain.studyTeam.dto.response.StudyApplicantResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamCreateResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamDetailResponse;
import backend.techeerzip.domain.studyTeam.dto.response.StudyTeamUpdateResponse;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudyTeamFacadeServiceImpl implements StudyTeamFacadeService {

    private final StudyTeamService studyTeamService;
    private final S3Service s3Service;
    private final CustomLogger log;

    public StudyTeamCreateResponse create(
            List<MultipartFile> resultImages, StudyTeamCreateRequest request) {
        final List<String> uploadedUrl = new ArrayList<>();
        try {
            final List<String> resultUrls =
                    s3Service.uploadMany(resultImages, "project-teams/result", "project-team");
            uploadedUrl.addAll(resultUrls);
            log.info("StudyTeam create: S3 업로드 완료 - 업로드 수={}", resultUrls.size());

            return studyTeamService.create(resultUrls, request);
        } catch (Exception e) {
            s3Service.deleteMany(uploadedUrl);
            log.error("StudyTeam create: 예외 발생, S3 업로드 롤백 - 삭제 대상={}", uploadedUrl, e);
            throw e;
        }
    }

    public StudyTeamUpdateResponse update(
            Long studyTeamId,
            Long userId,
            List<MultipartFile> resultImages,
            StudyTeamUpdateRequest request) {
        final boolean isResult = validateResultImages(resultImages, request.getDeleteImages());
        final List<String> resultImageUrls = new ArrayList<>();
        try {
            if (isResult) {
                final List<String> result =
                        s3Service.uploadMany(resultImages, "study-teams/result", "study-team");
                resultImageUrls.addAll(result);
                log.info("StudyTeam update: S3 업로드 완료 - 업로드 수={}", result.size());
            }
            return studyTeamService.update(studyTeamId, userId, resultImageUrls, request);
        } catch (Exception e) {
            if (!resultImageUrls.isEmpty()) {
                s3Service.deleteMany(resultImageUrls);
                log.error("StudyTeam update: 예외 발생, S3 업로드 롤백 - 삭제 대상={}", resultImageUrls, e);
            }
            throw e;
        }
    }

    public void closeRecruit(Long studyTeamId, Long userId) {
        log.info("StudyTeam closeRecruit: 모집 마감 요청 - teamId={}, userId={}", studyTeamId, userId);
        studyTeamService.close(studyTeamId, userId);
        log.info("StudyTeam closeRecruit: 모집 마감 완료 - teamId={}", studyTeamId);
    }

    public void softDeleteTeam(Long studyTeamId, Long userId) {
        log.info("StudyTeam softDelete: 소프트 삭제 요청 - teamId={}, userId={}", studyTeamId, userId);
        studyTeamService.softDelete(studyTeamId, userId);
        log.info("StudyTeam softDelete: 소프트 삭제 완료 - teamId={}", studyTeamId);
    }

    //    public ResponseEntity<List<GetStudyTeamResponse>> getUserStudyTeams(Long userId) {
    //        return null;
    //    }

    public StudyTeamDetailResponse getDetail(Long studyTeamId) {
        log.info("StudyTeam getDetail: 상세 조회 요청 - teamId={}", studyTeamId);
        final StudyTeamDetailResponse response =
                studyTeamService.updateViewCountAndGetDetail(studyTeamId);
        log.info("StudyTeam getDetail: 상세 조회 완료 - teamId={}", studyTeamId);
        return response;
    }

    //
    //    public ResponseEntity<List<StudyMemberResponse>> getStudyTeamMembers(Long studyTeamId) {
    //        return null;
    //    }

    public List<DM> applyToStudy(StudyTeamApplyRequest request, Long applicantId) {
        log.info("StudyTeam applyToStudy: 지원 요청 시작 - userId={}", applicantId);
        final List<DM> dms = studyTeamService.apply(request, applicantId);
        log.info("StudyTeam applyToStudy: 지원 요청 완료 - 전송 DM 수={}", dms.size());
        return dms;
    }

    public List<DM> cancelApplication(Long teamId, Long applicantId) {
        log.info(
                "StudyTeam cancelApplication: 지원 취소 요청 - teamId={}, userId={}",
                teamId,
                applicantId);
        final List<DM> dms = studyTeamService.cancelApplication(teamId, applicantId);
        log.info("StudyTeam cancelApplication: 지원 취소 완료 - 전송 DM 수={}", dms.size());
        return dms;
    }

    public List<StudyApplicantResponse> getApplicants(Long studyTeamId, Long userId) {
        log.info("StudyTeam getApplicants: 지원자 조회 요청 - teamId={}, userId={}", studyTeamId, userId);
        final List<StudyApplicantResponse> applicants =
                studyTeamService.getApplicants(studyTeamId, userId);
        log.info("StudyTeam getApplicants: 지원자 조회 완료 - 수={}", applicants.size());
        return applicants;
    }

    public List<DM> acceptApplicant(StudyApplicantRequest request, Long userId) {
        final Long teamId = request.studyId();
        final Long applicantId = request.applicantId();
        log.info(
                "StudyTeam acceptApplicant: 수락 요청 - teamId={}, applicantId={}, userId={}",
                teamId,
                applicantId,
                userId);
        final List<DM> dms = studyTeamService.acceptApplicant(teamId, userId, applicantId);
        log.info("StudyTeam acceptApplicant: 수락 완료 - 전송 DM 수={}", dms.size());
        return dms;
    }

    public List<DM> rejectApplicant(StudyApplicantRequest request, Long userId) {
        final Long teamId = request.studyId();
        final Long applicantId = request.applicantId();
        log.info(
                "StudyTeam rejectApplicant: 거절 요청 - teamId={}, applicantId={}, userId={}",
                teamId,
                applicantId,
                userId);
        final List<DM> dms = studyTeamService.rejectApplicant(teamId, userId, applicantId);
        log.info("StudyTeam rejectApplicant: 거절 완료 - 전송 DM 수={}", dms.size());
        return dms;
    }

    public void addMemberToStudyTeam(
            Long studyTeamId, Long userId, Long memberId, Boolean isLeader) {}
}
