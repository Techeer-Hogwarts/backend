package backend.techeerzip.domain.projectTeam.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import backend.techeerzip.domain.projectTeam.dto.request.GetTeamQueryRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ImageRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamUpdateRequest;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamCreateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamUpdateResponse;
import backend.techeerzip.domain.projectTeam.dto.response.StudyTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.dto.response.TeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamMainImageException;
import backend.techeerzip.domain.projectTeam.exception.ProjectTeamResultImageException;
import backend.techeerzip.domain.studyTeam.service.StudyTeamService;
import backend.techeerzip.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectTeamFacedServiceImpl implements ProjectTeamFacadeService {

    private final ProjectTeamService projectTeamService;
    private final StudyTeamService studyTeamService;
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
        final List<ProjectTeamGetAllResponse> projectResponses =
                projectTeamService.getAllTeams(request);
        final List<StudyTeamGetAllResponse> studyResponses = studyTeamService.getAllTeams(request);

        final List<TeamGetAllResponse> allTeams =
                Stream.concat(projectResponses.stream(), studyResponses.stream())
                        .sorted(Comparator.comparing(TeamGetAllResponse::getCreatedAt).reversed())
                        .toList();
        return ResponseEntity.ok(allTeams);
    }
}
