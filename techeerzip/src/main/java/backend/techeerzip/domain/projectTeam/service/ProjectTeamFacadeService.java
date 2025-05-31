package backend.techeerzip.domain.projectTeam.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectApplicantRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectSlackRequest.DM;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamApplyRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.response.GetAllTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamCreateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamUpdateResponse;

/**
 * 프로젝트 팀 관련 비즈니스 로직의 파사드 서비스입니다.
 * 
 * <p>처리 내용:
 * <ol>
 *     <li>프로젝트 팀 생성, 수정, 삭제 등의 기본 CRUD 작업</li>
 *     <li>이미지 업로드 및 S3 저장소 연동</li>
 *     <li>프로젝트/스터디 통합 조회 및 검색</li>
 *     <li>팀원 모집 및 지원자 관리</li>
 * </ol>
 */
public interface ProjectTeamFacadeService {

    /**
     * 프로젝트 팀의 상세 정보를 조회하고 조회수를 증가시킵니다.
     *
     * @param projectTeamId 조회할 팀 ID
     * @return 팀 상세 정보 응답 DTO
     */
    ProjectTeamDetailResponse getDetail(Long projectTeamId);

    /**
     * 프로젝트 팀을 생성하고 이미지를 업로드합니다.
     *
     * @param mainImage 메인 이미지 파일 (필수)
     * @param resultImages 결과 이미지 파일 리스트 (선택)
     * @param request 팀 생성 요청 정보
     * @return 생성된 팀 정보와 슬랙/인덱스 요청 정보
     */
    ProjectTeamCreateResponse create(
            MultipartFile mainImage,
            List<MultipartFile> resultImages,
            ProjectTeamCreateRequest request);

    /**
     * 프로젝트 팀 정보를 수정하고 이미지를 업데이트합니다.
     *
     * @param projectTeamId 수정할 팀 ID
     * @param userId 수정 요청자 ID
     * @param mainImage 메인 이미지 파일 (선택)
     * @param resultImages 결과 이미지 파일 리스트 (선택)
     * @param request 팀 수정 요청 정보
     * @return 수정된 팀 정보와 슬랙/인덱스 요청 정보
     */
    ProjectTeamUpdateResponse update(
            Long projectTeamId,
            Long userId,
            MultipartFile mainImage,
            List<MultipartFile> resultImages,
            ProjectTeamUpdateRequest request);

    /**
     * 프로젝트와 스터디 팀을 통합하여 조회합니다.
     *
     * @param request 조회 조건 및 정렬 기준
     * @return 통합된 팀 목록과 페이징 정보
     */
    GetAllTeamsResponse getAllProjectAndStudyTeams(GetTeamsQueryRequest request);

    /**
     * 프로젝트 팀의 모집을 종료합니다.
     *
     * @param projectTeamId 팀 ID
     * @param userId 요청자 ID
     */
    void closeRecruit(Long projectTeamId, Long userId);

    /**
     * 프로젝트 팀을 소프트 삭제 처리합니다.
     *
     * @param projectTeamId 팀 ID
     * @param userId 요청자 ID
     */
    void softDeleteTeam(Long projectTeamId, Long userId);

    /**
     * 프로젝트 팀에 지원합니다.
     *
     * @param request 지원 요청 정보
     * @param userId 지원자 ID
     * @return 슬랙 DM 알림 요청 리스트
     */
    List<DM> applyToProject(ProjectTeamApplyRequest request, Long userId);

    /**
     * 프로젝트 팀의 지원자 목록을 조회합니다.
     *
     * @param teamId 팀 ID
     * @param userId 요청자 ID
     * @return 지원자 목록
     */
    List<ProjectMemberApplicantResponse> getApplicants(Long teamId, Long userId);

    /**
     * 프로젝트 팀 지원을 취소합니다.
     *
     * @param teamId 팀 ID
     * @param applicantId 지원자 ID
     * @return 슬랙 DM 알림 요청 리스트
     */
    List<DM> cancelApplication(Long teamId, Long applicantId);

    /**
     * 프로젝트 팀 지원자를 승인합니다.
     *
     * @param request 승인 요청 정보
     * @param userId 요청자 ID
     * @return 슬랙 DM 알림 요청 리스트
     */
    List<DM> acceptApplicant(ProjectApplicantRequest request, Long userId);

    /**
     * 프로젝트 팀 지원자를 거절합니다.
     *
     * @param request 거절 요청 정보
     * @param userId 요청자 ID
     * @return 슬랙 DM 알림 요청 리스트
     */
    List<DM> rejectApplicant(ProjectApplicantRequest request, Long userId);
}
