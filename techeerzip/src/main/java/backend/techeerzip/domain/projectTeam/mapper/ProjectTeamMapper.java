package backend.techeerzip.domain.projectTeam.mapper;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.techeerzip.domain.projectTeam.dto.request.ImageRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.RecruitCounts;
import backend.techeerzip.domain.projectTeam.dto.request.TeamData;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse.MainImageInfo;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse.ProjectMemberInfo;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse.ResultImageInfo;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse.TeamStackDetail;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse.TeamStackDetail.StackInfo;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.entity.ProjectMainImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.exception.ProjectImageException;

public class ProjectTeamMapper {

    private ProjectTeamMapper() {}

    public static ProjectTeamCreateRequest toUpdateRequest(String body) {
        try {
            return new ObjectMapper().readValue(body, ProjectTeamCreateRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ProjectTeam toEntity(
            TeamData teamData, RecruitCounts recruitCounts, Boolean isRecruited) {
        return ProjectTeam.builder()
                .name(teamData.getName())
                .githubLink(teamData.getGithubLink())
                .notionLink(teamData.getNotionLink())
                .projectExplain(teamData.getProjectExplain())
                .recruitExplain(teamData.getRecruitExplain())
                .isFinished(teamData.getIsFinished())
                .isRecruited(isRecruited)
                .backendNum(recruitCounts.getBackendNum())
                .frontendNum(recruitCounts.getFrontedNum())
                .fullStackNum(recruitCounts.getFullStackNum())
                .devopsNum(recruitCounts.getDevOpsNum())
                .dataEngineerNum(recruitCounts.getDataEngineerNum())
                .build();
    }

    public static ProjectTeamGetAllResponse toGetAllResponse(ProjectTeam projectTeam) {
        return ProjectTeamGetAllResponse.builder()
                .id(projectTeam.getId())
                .name(projectTeam.getName())
                .projectExplain(projectTeam.getProjectExplain())
                .isDeleted(projectTeam.isDeleted())
                .isFinished(projectTeam.isFinished())
                .isRecruited(projectTeam.isRecruited())
                .frontendNum(projectTeam.getFrontendNum())
                .backendNum(projectTeam.getBackendNum())
                .devopsNum(projectTeam.getDevopsNum())
                .fullStackNum(projectTeam.getFullStackNum())
                .dataEngineerNum(projectTeam.getDataEngineerNum())
                .mainImages(
                        projectTeam.getMainImages().stream()
                                .map(ProjectMainImage::getImageUrl)
                                .toList())
                .teamStacks(
                        projectTeam.getTeamStacks().stream().map(TeamStackMapper::toDto).toList())
                .createdAt(projectTeam.getCreatedAt())
                .build();
    }

    public static ProjectTeamDetailResponse toDetailResponse(ProjectTeam projectTeam) {
        return ProjectTeamDetailResponse.builder()
                .id(projectTeam.getId())
                .isDeleted(projectTeam.isDeleted())
                .isRecruited(projectTeam.isRecruited())
                .isFinished(projectTeam.isFinished())
                .name(projectTeam.getName())
                .githubLink(projectTeam.getGithubLink())
                .notionLink(projectTeam.getNotionLink())
                .projectExplain(projectTeam.getProjectExplain())
                .frontendNum(projectTeam.getFrontendNum())
                .backendNum(projectTeam.getBackendNum())
                .devopsNum(projectTeam.getDevopsNum())
                .fullStackNum(projectTeam.getFullStackNum())
                .dataEngineerNum(projectTeam.getDataEngineerNum())
                .recruitExplain(projectTeam.getRecruitExplain())
                .likeCount(projectTeam.getLikeCount())
                .viewCount(projectTeam.getViewCount())
                .resultImages(
                        projectTeam.getResultImages().stream()
                                .map(
                                        i ->
                                                ResultImageInfo.builder()
                                                        .id(i.getId())
                                                        .isDeleted(i.isDeleted())
                                                        .imageUrl(i.getImageUrl())
                                                        .build())
                                .toList())
                .mainImages(
                        projectTeam.getMainImages().stream()
                                .map(
                                        i ->
                                                MainImageInfo.builder()
                                                        .id(i.getId())
                                                        .isDeleted(i.isDeleted())
                                                        .imageUrl(i.getImageUrl())
                                                        .build())
                                .toList())
                .teamStacks(
                        projectTeam.getTeamStacks().stream()
                                .map(
                                        ts ->
                                                TeamStackDetail.builder()
                                                        .id(ts.getId())
                                                        .isDeleted(ts.isDeleted())
                                                        .projectTeamId(ts.getId())
                                                        .isMain(ts.isMain())
                                                        .stack(
                                                                StackInfo.builder()
                                                                        .name(
                                                                                ts.getStack()
                                                                                        .getName())
                                                                        .category(
                                                                                ts.getStack()
                                                                                        .getCategory()
                                                                                        .name())
                                                                        .build())
                                                        .build())
                                .toList())
                .projectMember(
                        projectTeam.getProjectMembers().stream()
                                .map(
                                        pm ->
                                                ProjectMemberInfo.builder()
                                                        .id(pm.getId())
                                                        .userId(pm.getUser().getId())
                                                        .name(pm.getUser().getName())
                                                        .isLeader(pm.isLeader())
                                                        .teamRole(pm.getTeamRole().name())
                                                        .profileImage(
                                                                pm.getUser().getProfileImage())
                                                        .build())
                                .toList())
                .build();
    }

    public static ImageRequest separateImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new ProjectImageException();
        }
        if (files.size() == 1) {
            return new ImageRequest(files.getFirst(), List.of());
        }
        return new ImageRequest(files.getFirst(), files.subList(1, files.size()));
    }
}
