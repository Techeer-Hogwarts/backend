package backend.techeerzip.domain.projectTeam.mapper;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.techeerzip.domain.projectMember.entity.ProjectMember;
import backend.techeerzip.domain.projectTeam.dto.request.ImageRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
import backend.techeerzip.domain.projectTeam.dto.request.RecruitCounts;
import backend.techeerzip.domain.projectTeam.dto.request.projectTeamData;
import backend.techeerzip.domain.projectTeam.dto.response.LeaderInfo;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamDetailResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamGetAllResponse;
import backend.techeerzip.domain.projectTeam.dto.response.ProjectTeamUpdateResponse;
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
            projectTeamData teamData, RecruitCounts recruitCounts, Boolean isRecruited) {
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

    public static ProjectTeamDetailResponse toDetailResponse(
            ProjectTeam projectTeam, List<ProjectMember> projectMembers) {

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
                .resultImages(mapResultImages(projectTeam))
                .mainImages(mapMainImages(projectTeam))
                .teamStacks(mapTeamStacks(projectTeam))
                .projectMember(mapProjectMembers(projectMembers))
                .build();
    }

    private static List<ProjectTeamDetailResponse.ResultImageInfo> mapResultImages(
            ProjectTeam team) {
        return team.getResultImages().stream()
                .map(
                        i ->
                                ProjectTeamDetailResponse.ResultImageInfo.builder()
                                        .id(i.getId())
                                        .isDeleted(i.isDeleted())
                                        .imageUrl(i.getImageUrl())
                                        .build())
                .toList();
    }

    private static List<ProjectTeamDetailResponse.MainImageInfo> mapMainImages(ProjectTeam team) {
        return team.getMainImages().stream()
                .map(
                        i ->
                                ProjectTeamDetailResponse.MainImageInfo.builder()
                                        .id(i.getId())
                                        .isDeleted(i.isDeleted())
                                        .imageUrl(i.getImageUrl())
                                        .build())
                .toList();
    }

    private static List<ProjectTeamDetailResponse.TeamStackDetail> mapTeamStacks(ProjectTeam team) {
        return team.getTeamStacks().stream()
                .map(
                        ts ->
                                ProjectTeamDetailResponse.TeamStackDetail.builder()
                                        .id(ts.getId())
                                        .isDeleted(ts.isDeleted())
                                        .projectTeamId(team.getId())
                                        .isMain(ts.isMain())
                                        .stack(
                                                ProjectTeamDetailResponse.TeamStackDetail.StackInfo
                                                        .builder()
                                                        .name(ts.getStack().getName())
                                                        .category(
                                                                ts.getStack().getCategory().name())
                                                        .build())
                                        .build())
                .toList();
    }

    private static List<ProjectTeamDetailResponse.ProjectMemberInfo> mapProjectMembers(
            List<ProjectMember> members) {
        return members.stream()
                .map(
                        pm ->
                                ProjectTeamDetailResponse.ProjectMemberInfo.builder()
                                        .id(pm.getId())
                                        .userId(pm.getUser().getId())
                                        .name(pm.getUser().getName())
                                        .isLeader(pm.isLeader())
                                        .teamRole(pm.getTeamRole().name())
                                        .profileImage(pm.getUser().getProfileImage())
                                        .build())
                .toList();
    }

    public static ProjectTeamUpdateResponse toUpdatedResponse(
            Long projectTeamId, ProjectTeam team, List<LeaderInfo> leaders) {
        return ProjectTeamUpdateResponse.builder()
                .id(projectTeamId)
                .slackRequest(ProjectSlackMapper.toChannelRequest(team, leaders))
                .indexRequest(TeamIndexMapper.toProjectRequest(team))
                .build();
    }

    public static ProjectTeamUpdateResponse toIndexOnlyUpdateResponse(
            Long projectTeamId, ProjectTeam team) {
        return ProjectTeamUpdateResponse.builder()
                .id(projectTeamId)
                .indexRequest(TeamIndexMapper.toProjectRequest(team))
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
