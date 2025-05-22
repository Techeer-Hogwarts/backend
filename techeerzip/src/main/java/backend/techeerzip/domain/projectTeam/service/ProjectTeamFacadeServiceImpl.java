package backend.techeerzip.domain.projectTeam.service;

import backend.techeerzip.domain.projectTeam.dto.request.SlackRequest.DM;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectApplicantRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamApplyRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectMemberApplicantResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamCreateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamUpdateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.StudyTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.dto.response.TeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.dto.response.TeamUnionSliceYoungInfo;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMainImageException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamResultImageException;
import backend.techeerzip.domain.projectTeam.mapper.TeamViewMapper;
import backend.techeerzip.domain.projectTeam.repository.querydsl.TeamUnionViewDslRepository;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
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

    // slack 은 애바?
    // index

    private static boolean validateMainImage(MultipartFile mainImage, List<Long> deleteMainImages) {
        if ((mainImage == null || mainImage.isEmpty()) && deleteMainImages.isEmpty()) {
            return false;
        }
        if (mainImage != null && deleteMainImages.size() == 1) {
            return true;
        }
        throw new ProjectTeamMainImageException();
    }

    private static boolean validateResultImages(
            List<MultipartFile> resultImages, List<Long> deleteResultImages) {
        if (resultImages == null || resultImages.isEmpty()) {
            return false;
        }
        int count = resultImages.size() - deleteResultImages.size();
        if (count > 10 || count < 0) {
            throw new ProjectTeamResultImageException();
        }
        return count != 0;
    }

    private static boolean isOnlyProject(List<PositionNumType> numTypes) {
        return !numTypes.isEmpty();
    }

    private static boolean isOnlyStudy(List<TeamType> teamTypes) {
        return !teamTypes.isEmpty() && teamTypes.getFirst().equals(TeamType.STUDY);
    }

    private static List<TeamGetAllResponse> toTeamGetAllResponse(
            List<ProjectTeamGetAllResponse> projectResponses,
            List<StudyTeamGetAllResponse> studyResponses) {
        return Stream.concat(projectResponses.stream(), studyResponses.stream())
                .sorted(Comparator.comparing(TeamGetAllResponse::getCreatedAt).reversed())
                .toList();
    }

    public ProjectTeamDetailResponse getDetail(Long projectTeamId) {
        return projectTeamService.updateViewCountAndGetDetail(projectTeamId);
    }

    public ProjectTeamCreateResponse create(
            MultipartFile mainImage,
            List<MultipartFile> resultImages,
            ProjectTeamCreateRequest request) {
        // S3 upload
        // 롤백 구현 필요
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

            // Transaction
            return projectTeamService.create(mainUrl, resultUrls, request);
        } catch (Exception e) {
            s3Service.deleteMany(uploadedUrl);
            log.error("ProjectTeam Create: s3 롤백", uploadedUrl);
            throw e;
        }
    }

    public ProjectTeamUpdateResponse update(
            Long projectTeamId,
            Long userId,
            MultipartFile mainImage,
            List<MultipartFile> resultImages,
            ProjectTeamUpdateRequest request) {
        // If S3 upload
        /* 5. MainImage 개수 검증 */
        final boolean isMain = validateMainImage(mainImage, request.getDeleteMainImages());
        /* 6. ResultImage 개수 검증 */
        final boolean isResult =
                validateResultImages(resultImages, request.getDeleteResultImages());
        /* 8. S3 이미지 업로드 */
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

    public List<TeamGetAllResponse> getAllProjectAndStudyTeams(
            GetTeamsQueryRequest request) {
        final GetTeamsQuery query = TeamViewMapper.mapToQuery(request);
        final List<PositionNumType> numTypes = query.getPositionNumTypes();
        final List<TeamType> teamTypes = query.getTeamTypes();
        final Long limit = query.getLimit();
        final Boolean isRecruited = query.getIsRecruited();
        final Boolean isFinished = query.getIsFinished();
        if (isOnlyProject(numTypes)) {
            return new ArrayList<>(
                            projectTeamService.getYoungTeams(
                                    numTypes, isRecruited, isFinished, limit));
        }
        if (isOnlyStudy(teamTypes)) {
            return new ArrayList<>(
                            studyTeamService.getYoungTeams(isRecruited, isFinished, limit));
        }
        final TeamUnionSliceYoungInfo views =
                teamUnionViewDslRepository.fetchSliceBeforeCreatedAtDescCursor(query);

        final List<ProjectTeamGetAllResponse> projectResponses =
                projectTeamService.getYoungTeamsById(views.projectsId(), isRecruited, isFinished);

        final List<StudyTeamGetAllResponse> studyResponses =
                studyTeamService.getYoungTeamsById(views.studiesId(), isRecruited, isFinished);

        return toTeamGetAllResponse(projectResponses, studyResponses);
    }

    public void closeRecruit(Long projectTeamId, Long userId) {
        projectTeamService.close(projectTeamId, userId);
    }

    public void softDeleteTeam(Long projectTeamId, Long userId) {
        projectTeamService.softDelete(projectTeamId, userId);
    }

    public List<ProjectMemberApplicantResponse> getApplicants(
            Long teamId, Long userId) {
        return projectTeamService.getApplicants(teamId, userId);
    }

    public List<DM> applyToProject(
            ProjectTeamApplyRequest request, Long userId) {
        return projectTeamService.apply(request, userId);
    }


    public List<DM> cancelApplication(Long teamId, Long applicantId) {
        return projectTeamService.cancelApplication(teamId, applicantId);
    }

    public List<DM> acceptApplicant(
            ProjectApplicantRequest request, Long userId) {
        final Long teamId = request.teamId();
        final Long applicantId = request.applicantId();
        return projectTeamService.acceptApplicant(teamId, userId, applicantId);
    }

    public List<DM> rejectApplicant(
            ProjectApplicantRequest request, Long userId) {
        final Long teamId = request.teamId();
        final Long applicantId = request.applicantId();
        return projectTeamService.rejectApplicant(teamId, userId, applicantId);
    }

    //    public ResponseEntity<List<ProjectUserTeamsResponse>> getUserTeams(Long userId) {
    //        return ResponseEntity.ok(projectTeamService.getUserTeams(userId));
    //    }

}
