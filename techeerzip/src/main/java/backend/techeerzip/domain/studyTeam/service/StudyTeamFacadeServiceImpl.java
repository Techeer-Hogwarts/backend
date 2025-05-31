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
            log.debug("ProjectTeam Create: s3 result 업로드 완료");
            // Transaction
            return studyTeamService.create(resultUrls, request);
        } catch (Exception e) {
            s3Service.deleteMany(uploadedUrl);
            log.error("ProjectTeam Create: s3 롤백", uploadedUrl);
            throw e;
        }
    }

    public StudyTeamUpdateResponse update(
            Long studyTeamId,
            Long userId,
            List<MultipartFile> resultImages,
            StudyTeamUpdateRequest request) {
        /* 6. ResultImage 개수 검증 */
        final boolean isResult = validateResultImages(resultImages, request.getDeleteImages());
        /* 8. S3 이미지 업로드 */
        final List<String> resultImageUrls = new ArrayList<>();
        try {
            if (isResult) {
                final List<String> result =
                        s3Service.uploadMany(resultImages, "study-teams/main", "study-team");
                resultImageUrls.addAll(result);
            }
            return studyTeamService.update(studyTeamId, userId, resultImageUrls, request);
        } catch (Exception e) {
            if (!resultImageUrls.isEmpty()) {
                s3Service.deleteMany(resultImageUrls);
            }
            throw e;
        }
    }

    public void closeRecruit(Long studyTeamId, Long userId) {
        studyTeamService.close(studyTeamId, userId);
    }

    public void softDeleteTeam(Long studyTeamId, Long userId) {
        studyTeamService.softDelete(studyTeamId, userId);
    }

    //    public ResponseEntity<List<GetStudyTeamResponse>> getUserStudyTeams(Long userId) {
    //        return null;
    //    }

    public StudyTeamDetailResponse getDetail(Long studyTeamId) {
        return studyTeamService.updateViewCountAndGetDetail(studyTeamId);
    }

    //
    //    public ResponseEntity<List<StudyMemberResponse>> getStudyTeamMembers(Long studyTeamId) {
    //        return null;
    //    }

    public List<DM> applyToStudy(StudyTeamApplyRequest request, Long applicantId) {
        return studyTeamService.apply(request, applicantId);
    }

    public List<DM> cancelApplication(Long teamId, Long applicantId) {
        return studyTeamService.cancelApplication(teamId, applicantId);
    }

    public List<StudyApplicantResponse> getApplicants(Long studyTeamId, Long userId) {
        return studyTeamService.getApplicants(studyTeamId, userId);
    }

    public List<DM> acceptApplicant(StudyApplicantRequest request, Long userId) {
        final Long teamId = request.studyId();
        final Long applicantId = request.applicantId();
        return studyTeamService.acceptApplicant(teamId, userId, applicantId);
    }

    public List<DM> rejectApplicant(StudyApplicantRequest request, Long userId) {
        final Long teamId = request.studyId();
        final Long applicantId = request.applicantId();
        return studyTeamService.rejectApplicant(teamId, userId, applicantId);
    }

    public void addMemberToStudyTeam(
            Long studyTeamId, Long userId, Long memberId, Boolean isLeader) {}
}
