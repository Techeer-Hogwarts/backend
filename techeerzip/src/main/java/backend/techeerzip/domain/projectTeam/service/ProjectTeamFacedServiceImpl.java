package backend.techeerzip.domain.projectTeam.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.common.dto.SlackRequest;
import backend.techeerzip.domain.projectTeam.dto.request.EmptyResponse;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamQueryRequest.GetTeamQuery;
import backend.techeerzip.domain.projectTeam.dto.request.ImageRequest;
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
import backend.techeerzip.domain.projectTeam.repository.querydsl.TeamUnionViewDslRepository;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import backend.techeerzip.domain.studyTeam.service.StudyTeamService;
import backend.techeerzip.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectTeamFacedServiceImpl implements ProjectTeamFacadeService {

    private final ProjectTeamService projectTeamService;
    private final StudyTeamService studyTeamService;
    private final TeamUnionViewDslRepository teamUnionViewDslRepository;
    private final S3Service s3Service;

    // slack 은 애바?
    // index

    private static void validateMainImage(MultipartFile mainImage, List<Long> deleteMainImages) {
        if (mainImage == null && deleteMainImages.isEmpty()) {
            return;
        }
        if (mainImage != null && deleteMainImages.size() == 1) {
            return;
        }
        throw new ProjectTeamMainImageException();
    }

    private static void validateResultImages(
            List<MultipartFile> resultImages, List<Long> deleteResultImages) {
        int count = resultImages.size() - deleteResultImages.size();
        if (count > 10) {
            throw new ProjectTeamResultImageException();
        }
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

    public ResponseEntity<ProjectTeamDetailResponse> getDetail(Long projectTeamId) {
        return ResponseEntity.ok(projectTeamService.updateViewCountAndGetDetail(projectTeamId));
    }

    public ResponseEntity<Long> create(
            ImageRequest imageRequest, ProjectTeamCreateRequest request) {
        // S3 upload
        // 롤백 구현 필요
        final List<String> mainImagesUrl =
                s3Service.upload(imageRequest.mainImage(), "project-teams/main", "project-team");
        final List<String> resultImageUrls =
                s3Service.uploadMany(
                        imageRequest.resultImages(), "project-teams/result", "project-team");
        // Transaction
        final ProjectTeamCreateResponse response =
                projectTeamService.create(mainImagesUrl, resultImageUrls, request);
        // Index Service
        // Slack Service
        return ResponseEntity.ok(response.id());
    }

    public ResponseEntity<Long> update(
            Long projectTeamId,
            Long userId,
            MultipartFile mainImage,
            List<MultipartFile> resultImages,
            ProjectTeamUpdateRequest request) {
        // If S3 upload
        /* 5. MainImage 개수 검증 */
        validateMainImage(mainImage, request.getDeleteMainImages());
        /* 6. ResultImage 개수 검증 */
        validateResultImages(resultImages, request.getDeleteResultImages());
        /* 8. S3 이미지 업로드 */
        final List<String> mainImagesUrl =
                s3Service.upload(mainImage, "project-teams/main", "project-team");
        final List<String> resultImageUrls =
                s3Service.uploadMany(resultImages, "project-teams/main", "project-team");
        // Transaction
        final ProjectTeamUpdateResponse response =
                projectTeamService.update(
                        projectTeamId, userId, mainImagesUrl, resultImageUrls, request);

        if (response.slackRequest() != null) {
            // send Slack
        }
        // Index Service
        return ResponseEntity.ok(response.id());
    }

    public ResponseEntity<List<TeamGetAllResponse>> getAllProjectAndStudyTeams(
            GetTeamQueryRequest request) {
        final GetTeamQuery query = request.toQuery();
        final List<PositionNumType> numTypes = query.getPositionNumTypes();
        final List<TeamType> teamTypes = query.getTeamTypes();
        final Long limit = query.getLimit();
        final Boolean isRecruited = query.getIsRecruited();
        final Boolean isFinished = query.getIsFinished();
        if (isOnlyProject(numTypes)) {
            return ResponseEntity.ok(
                    new ArrayList<>(
                            projectTeamService.getYoungTeams(
                                    numTypes, isRecruited, isFinished, limit)));
        }
        if (isOnlyStudy(teamTypes)) {
            return ResponseEntity.ok(
                    new ArrayList<>(
                            studyTeamService.getYoungTeams(isRecruited, isFinished, limit)));
        }
        final TeamUnionSliceYoungInfo views =
                teamUnionViewDslRepository.fetchSliceBeforeCreatedAtDescCursor(query);

        final List<ProjectTeamGetAllResponse> projectResponses =
                projectTeamService.getYoungTeamsById(views.projectsId(), isRecruited, isFinished);

        final List<StudyTeamGetAllResponse> studyResponses =
                studyTeamService.getYoungTeamsById(views.studiesId(), isRecruited, isFinished);

        return ResponseEntity.ok(toTeamGetAllResponse(projectResponses, studyResponses));
    }

    public ResponseEntity<EmptyResponse> closeRecruit(Long projectTeamId, Long userId) {
        projectTeamService.close(projectTeamId, userId);
        return ResponseEntity.ok(new EmptyResponse());
    }

    public ResponseEntity<EmptyResponse> softDeleteTeam(Long projectTeamId, Long userId) {
        projectTeamService.softDelete(projectTeamId, userId);
        return ResponseEntity.ok(new EmptyResponse());
    }

    public ResponseEntity<EmptyResponse> applyToProject(
            ProjectTeamApplyRequest request, Long userId) {
        SlackRequest slackRequest = projectTeamService.apply(request, userId);
        // slack send
        return ResponseEntity.ok(new EmptyResponse());
    }

    public ResponseEntity<List<ProjectMemberApplicantResponse>> getApplicants(
            Long teamId, Long userId) {
        final List<ProjectMemberApplicantResponse> applicants =
                projectTeamService.getApplicants(teamId, userId);
        return ResponseEntity.ok(applicants);
    }

    public ResponseEntity<EmptyResponse> cancelApplication(Long teamId, Long applicantId) {
        projectTeamService.cancelApplication(teamId, applicantId);
        return ResponseEntity.ok(new EmptyResponse());
    }

    public ResponseEntity<EmptyResponse> acceptApplicant(
            ProjectApplicantRequest request, Long userId) {
        final Long teamId = request.teamId();
        final Long applicantId = request.applicantId();
        projectTeamService.acceptApplicant(teamId, userId, applicantId);
        return ResponseEntity.ok(new EmptyResponse());
    }

    public ResponseEntity<EmptyResponse> rejectApplicant(
            ProjectApplicantRequest request, Long userId) {
        final Long teamId = request.teamId();
        final Long applicantId = request.applicantId();
        projectTeamService.rejectApplicant(teamId, userId, applicantId);
        return ResponseEntity.ok(new EmptyResponse());
    }

    //    public ResponseEntity<List<ProjectUserTeamsResponse>> getUserTeams(Long userId) {
    //        return ResponseEntity.ok(projectTeamService.getUserTeams(userId));
    //    }

}
