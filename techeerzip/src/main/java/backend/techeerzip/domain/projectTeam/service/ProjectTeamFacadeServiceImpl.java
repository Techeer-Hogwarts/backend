package backend.techeerzip.domain.projectTeam.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectMember.dto.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectTeam.dto.request.GetProjectTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetStudyTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectApplicantRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectSlackRequest.DM;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamApplyRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.response.GetAllTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectSliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamCreateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamUpdateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.SliceNextCursor;
import backend.techeerzip.domain.projectTeam.dto.response.SliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.dto.response.TeamUnionSliceResult;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMainImageException;
import backend.techeerzip.domain.projectTeam.exception.TeamResultImageException;
import backend.techeerzip.domain.projectTeam.mapper.TeamQueryMapper;
import backend.techeerzip.domain.projectTeam.repository.querydsl.TeamUnionViewDslRepository;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.domain.studyTeam.dto.response.StudySliceTeamsResponse;
import backend.techeerzip.domain.studyTeam.service.StudyTeamService;
import backend.techeerzip.global.logger.CustomLogger;
import backend.techeerzip.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectTeamFacadeServiceImpl implements ProjectTeamFacadeService {

    private final ProjectTeamService projectTeamService;
    private final StudyTeamService studyTeamService;
    private final TeamUnionViewDslRepository teamUnionViewDslRepository;
    private final S3Service s3Service;
    private final CustomLogger log;

    /**
     * 메인 이미지의 유효성을 검증합니다.
     *
     * @param mainImage 업로드할 메인 이미지
     * @param deleteMainImages 삭제할 메인 이미지 ID 리스트
     * @return 메인 이미지 업데이트 필요 여부
     * @throws ProjectTeamMainImageException 메인 이미지 검증 실패 시
     */
    private static boolean validateMainImage(MultipartFile mainImage, List<Long> deleteMainImages) {
        if ((mainImage == null || mainImage.isEmpty()) && deleteMainImages.isEmpty()) {
            return false;
        }
        if (mainImage != null && deleteMainImages.size() == 1) {
            return true;
        }
        throw new ProjectTeamMainImageException();
    }

    /**
     * 결과 이미지들의 유효성을 검증합니다.
     *
     * @param resultImages 업로드할 결과 이미지 리스트
     * @param deleteResultImages 삭제할 결과 이미지 ID 리스트
     * @return 결과 이미지 업데이트 필요 여부
     * @throws TeamResultImageException 결과 이미지 검증 실패 시
     */
    public static boolean validateResultImages(
            List<MultipartFile> resultImages, List<Long> deleteResultImages) {
        if (resultImages == null || resultImages.isEmpty()) {
            return false;
        }
        int count = resultImages.size() - deleteResultImages.size();
        if (count > 10 || count < 0) {
            throw new TeamResultImageException();
        }
        return count != 0;
    }

    private static boolean isOnlyProject(List<TeamType> teamTypes) {
        return !teamTypes.isEmpty() && teamTypes.getFirst().equals(TeamType.PROJECT);
    }

    private static boolean isOnlyStudy(List<TeamType> teamTypes) {
        return !teamTypes.isEmpty() && teamTypes.getFirst().equals(TeamType.STUDY);
    }

    public ProjectTeamDetailResponse getDetail(Long projectTeamId) {
        return projectTeamService.updateViewCountAndGetDetail(projectTeamId);
    }

    /**
     * 프로젝트 팀을 생성하고 이미지 업로드 처리 및 실패 시 롤백을 수행합니다.
     *
     * <p>1. 메인 이미지 S3 업로드<br>
     * 2. 결과 이미지 S3 업로드<br>
     * 3. 프로젝트 팀 생성 서비스 호출<br>
     * 4. 실패 시 업로드된 이미지 전체 S3에서 삭제
     *
     * @param mainImage 메인 이미지 파일
     * @param resultImages 결과 이미지 파일 리스트
     * @param request 프로젝트 생성 요청 객체
     * @return 생성된 프로젝트 팀의 응답 객체
     * @throws RuntimeException 이미지 업로드나 서비스 처리 중 예외 발생 시 그대로 전파
     */
    public ProjectTeamCreateResponse create(
            MultipartFile mainImage,
            List<MultipartFile> resultImages,
            ProjectTeamCreateRequest request) {

        final List<String> uploadedUrl = new ArrayList<>();
        try {
            final List<String> mainUrl =
                    s3Service.upload(mainImage, "project-teams/main", "project-team");
            uploadedUrl.addAll(mainUrl);
            log.debug("ProjectTeam Create: s3 main 업로드 완료");

            final List<String> resultUrls =
                    s3Service.uploadMany(resultImages, "project-teams/result", "project-team");
            uploadedUrl.addAll(resultUrls);
            log.debug("ProjectTeam Create: s3 result 업로드 완료");

            return projectTeamService.create(mainUrl, resultUrls, request);
        } catch (Exception e) {
            s3Service.deleteMany(uploadedUrl);
            log.error("ProjectTeam Create: s3 롤백", uploadedUrl);
            throw e;
        }
    }

    /**
     * 프로젝트 팀을 수정하고 필요한 경우 이미지 업로드 및 삭제 처리를 수행합니다.
     *
     * <p>1. 수정 대상 이미지 여부 검증<br>
     * 2. 새 이미지가 있는 경우 S3 업로드<br>
     * 3. 프로젝트 팀 업데이트 서비스 호출<br>
     * 4. 실패 시 업로드된 이미지 S3에서 삭제
     *
     * @param projectTeamId 수정 대상 프로젝트 팀 ID
     * @param userId 요청자 ID
     * @param mainImage 새 메인 이미지 파일 (null 가능)
     * @param resultImages 새 결과 이미지 파일 리스트 (null 가능)
     * @param request 프로젝트 수정 요청 객체
     * @return 수정된 프로젝트 팀의 응답 객체
     * @throws RuntimeException 이미지 업로드나 서비스 처리 중 예외 발생 시 그대로 전파
     */
    public ProjectTeamUpdateResponse update(
            Long projectTeamId,
            Long userId,
            MultipartFile mainImage,
            List<MultipartFile> resultImages,
            ProjectTeamUpdateRequest request) {

        final boolean isMain = validateMainImage(mainImage, request.getDeleteMainImages());
        final boolean isResult =
                validateResultImages(resultImages, request.getDeleteResultImages());
        final List<String> mainImagesUrl = new ArrayList<>();
        final List<String> resultImageUrls = new ArrayList<>();
        try {
            if (isMain) {
                final List<String> main =
                        s3Service.upload(mainImage, "project-teams/main", "project-team");
                mainImagesUrl.addAll(main);
            }
            if (isResult) {
                final List<String> result =
                        s3Service.uploadMany(resultImages, "project-teams/result", "project-team");
                resultImageUrls.addAll(result);
            }
            return projectTeamService.update(
                    projectTeamId, userId, mainImagesUrl, resultImageUrls, request);
        } catch (Exception e) {
            if (!mainImagesUrl.isEmpty() || !resultImageUrls.isEmpty()) {
                resultImageUrls.addAll(mainImagesUrl);
                s3Service.deleteMany(resultImageUrls);
            }
            throw e;
        }
    }

    /**
     * 프로젝트와 스터디 팀을 조건에 따라 조회합니다. (커서 기반 페이징)
     *
     * <p>1. 정렬 조건이 없을 경우 기본값 설정<br>
     * 2. 프로젝트만 조회<br>
     * 3. 스터디만 조회<br>
     * 4. 둘 다 포함된 경우 TeamUnionView를 통해 한 번에 조회
     *
     * @param queryRequest 팀 전체 조회 쿼리 요청 객체
     * @return 페이징된 팀 목록과 다음 커서 정보가 포함된 응답 객체
     */
    public GetAllTeamsResponse getAllProjectAndStudyTeams(GetTeamsQueryRequest queryRequest) {
        final GetTeamsQueryRequest request = queryRequest.withDefaultSortType();
        final List<TeamType> teamTypes =
                Optional.ofNullable(request.getTeamTypes()).orElse(List.of());
        if (isOnlyProject(teamTypes)) {
            final GetProjectTeamsQuery projectQuery = TeamQueryMapper.mapToProjectQuery(request);
            return projectTeamService.getSliceTeams(projectQuery);
        }
        if (isOnlyStudy(teamTypes)) {
            final GetStudyTeamsQuery studyQuery = TeamQueryMapper.mapToStudyQuery(request);
            return studyTeamService.getSliceTeams(studyQuery);
        }

        final GetTeamsQuery query = TeamQueryMapper.mapToTeamsQuery(request);
        final TeamUnionSliceResult slicedResult = teamUnionViewDslRepository.fetchSliceTeams(query);

        final List<ProjectSliceTeamsResponse> projectResponses =
                projectTeamService.getProjectTeamsById(slicedResult.getProjectIds());

        final List<StudySliceTeamsResponse> studyResponses =
                studyTeamService.getStudyTeamsById(slicedResult.getStudyIds());
        final SliceNextCursor sliceNextInfo = slicedResult.getSliceNextInfo();
        final List<SliceTeamsResponse> responses =
                TeamQueryMapper.toTeamGetAllResponse(
                        projectResponses, studyResponses, request.getSortType());

        return new GetAllTeamsResponse(responses, sliceNextInfo);
    }

    public void closeRecruit(Long projectTeamId, Long userId) {
        projectTeamService.close(projectTeamId, userId);
    }

    public void softDeleteTeam(Long projectTeamId, Long userId) {
        projectTeamService.softDelete(projectTeamId, userId);
    }

    public List<ProjectMemberApplicantResponse> getApplicants(Long teamId, Long userId) {
        return projectTeamService.getApplicants(teamId, userId);
    }

    public List<DM> applyToProject(ProjectTeamApplyRequest request, Long userId) {
        return projectTeamService.apply(request, userId);
    }

    public List<DM> cancelApplication(Long teamId, Long applicantId) {
        return projectTeamService.cancelApplication(teamId, applicantId);
    }

    public List<DM> acceptApplicant(ProjectApplicantRequest request, Long userId) {
        final Long teamId = request.teamId();
        final Long applicantId = request.applicantId();
        return projectTeamService.acceptApplicant(teamId, userId, applicantId);
    }

    public List<DM> rejectApplicant(ProjectApplicantRequest request, Long userId) {
        final Long teamId = request.teamId();
        final Long applicantId = request.applicantId();
        return projectTeamService.rejectApplicant(teamId, userId, applicantId);
    }

    //    public ResponseEntity<List<ProjectUserTeamsResponse>> getUserTeams(Long userId) {
    //        return ResponseEntity.ok(projectTeamService.getUserTeams(userId));
    //    }

}
