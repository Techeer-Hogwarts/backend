package backend.techeerzip.domain.projectTeam.mapper;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.techeerzip.domain.projectTeam.dto.request.ImageRequest;
import backend.techeerzip.domain.projectTeam.dto.request.ProjectTeamCreateRequest;
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
