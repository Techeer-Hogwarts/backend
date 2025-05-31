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

/**
 * 스터디 팀 관련 비즈니스 로직의 파사드 서비스입니다.
 * 
 * <p>처리 내용:
 * <ol>
 *     <li>스터디 팀 생성, 수정, 삭제 등의 기본 CRUD 작업</li>
 *     <li>이미지 업로드 및 S3 저장소 연동</li>
 *     <li>팀원 모집 및 지원자 관리</li>
 *     <li>팀원 추가 및 리더 권한 관리</li>
 * </ol>
 */
public interface StudyTeamFacadeService {

    /**
     * 스터디 팀을 생성하고 이미지를 업로드합니다.
     *
     * @param files 결과 이미지 파일 리스트
     * @param request 팀 생성 요청 정보
     * @return 생성된 팀 정보와 슬랙/인덱스 요청 정보
     */
    StudyTeamCreateResponse create(List<MultipartFile> files, StudyTeamCreateRequest request);

    /**
     * 스터디 팀 정보를 수정하고 이미지를 업데이트합니다.
     *
     * @param studyTeamId 수정할 팀 ID
     * @param userId 수정 요청자 ID
     * @param files 결과 이미지 파일 리스트
     * @param request 팀 수정 요청 정보
     * @return 수정된 팀 정보와 슬랙/인덱스 요청 정보
     */
    StudyTeamUpdateResponse update(
            Long studyTeamId,
            Long userId,
            List<MultipartFile> files,
            StudyTeamUpdateRequest request);

    /**
     * 스터디 팀의 모집을 종료합니다.
     *
     * @param studyTeamId 팀 ID
     * @param userId 요청자 ID
     */
    void closeRecruit(Long studyTeamId, Long userId);

    /**
     * 스터디 팀을 소프트 삭제 처리합니다.
     *
     * @param studyTeamId 팀 ID
     * @param userId 요청자 ID
     */
    void softDeleteTeam(Long studyTeamId, Long userId);

    /**
     * 스터디 팀의 상세 정보를 조회하고 조회수를 증가시킵니다.
     *
     * @param studyTeamId 조회할 팀 ID
     * @return 팀 상세 정보 응답 DTO
     */
    StudyTeamDetailResponse getDetail(Long studyTeamId);

    /**
     * 스터디 팀에 지원합니다.
     *
     * @param request 지원 요청 정보
     * @param userId 지원자 ID
     * @return 슬랙 DM 알림 요청 리스트
     */
    List<DM> applyToStudy(StudyTeamApplyRequest request, Long userId);

    /**
     * 스터디 팀 지원을 취소합니다.
     *
     * @param studyTeamId 팀 ID
     * @param userId 지원자 ID
     * @return 슬랙 DM 알림 요청 리스트
     */
    List<DM> cancelApplication(Long studyTeamId, Long userId);

    /**
     * 스터디 팀의 지원자 목록을 조회합니다.
     *
     * @param studyTeamId 팀 ID
     * @param userId 요청자 ID
     * @return 지원자 목록
     */
    List<StudyApplicantResponse> getApplicants(Long studyTeamId, Long userId);

    /**
     * 스터디 팀 지원자를 승인합니다.
     *
     * @param request 승인 요청 정보
     * @param userId 요청자 ID
     * @return 슬랙 DM 알림 요청 리스트
     */
    List<DM> acceptApplicant(StudyApplicantRequest request, Long userId);

    /**
     * 스터디 팀 지원자를 거절합니다.
     *
     * @param request 거절 요청 정보
     * @param userId 요청자 ID
     * @return 슬랙 DM 알림 요청 리스트
     */
    List<DM> rejectApplicant(StudyApplicantRequest request, Long userId);

    /**
     * 스터디 팀에 새로운 멤버를 추가합니다.
     *
     * @param studyTeamId 팀 ID
     * @param userId 요청자 ID
     * @param memberId 추가할 멤버 ID
     * @param isLeader 리더 권한 부여 여부
     */
    void addMemberToStudyTeam(Long studyTeamId, Long userId, Long memberId, Boolean isLeader);
}
