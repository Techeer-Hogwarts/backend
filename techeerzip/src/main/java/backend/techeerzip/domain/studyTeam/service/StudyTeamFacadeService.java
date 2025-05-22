package backend.techeerzip.domain.studyTeam.service;

import java.util.List;

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

public interface StudyTeamFacadeService {

    StudyTeamCreateResponse create(List<MultipartFile> files, StudyTeamCreateRequest request);

    StudyTeamUpdateResponse update(
            Long studyTeamId,
            Long userId,
            List<MultipartFile> files,
            StudyTeamUpdateRequest request);

    void closeRecruit(Long studyTeamId, Long userId);

    void softDeleteTeam(Long studyTeamId, Long userId);

    StudyTeamDetailResponse getDetail(Long studyTeamId);

    // ResponseEntity<List<StudyMemberResponse>> getStudyTeamMembers(Long studyTeamId);

    List<DM> applyToStudy(StudyTeamApplyRequest request, Long userId);

    List<DM> cancelApplication(Long studyTeamId, Long userId);

    List<StudyApplicantResponse> getApplicants(Long studyTeamId, Long userId);

    List<DM> acceptApplicant(StudyApplicantRequest request, Long userId);

    List<DM> rejectApplicant(StudyApplicantRequest request, Long userId);

    void addMemberToStudyTeam(Long studyTeamId, Long userId, Long memberId, Boolean isLeader);
}
