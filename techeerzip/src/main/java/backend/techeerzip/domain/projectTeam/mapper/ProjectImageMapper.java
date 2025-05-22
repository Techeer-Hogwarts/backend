package backend.techeerzip.domain.projectTeam.mapper;

import java.util.List;

import backend.techeerzip.domain.projectTeam.entity.ProjectMainImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectResultImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;

public class ProjectImageMapper {

    private ProjectImageMapper() {}

    public static ProjectMainImage toMainEntity(String url, ProjectTeam projectTeam) {
        return ProjectMainImage.builder().imageUrl(url).projectTeam(projectTeam).build();
    }

    public static ProjectResultImage toResultEntity(String url, ProjectTeam projectTeam) {
        return ProjectResultImage.builder().imageUrl(url).projectTeam(projectTeam).build();
    }

    public static List<ProjectResultImage> toResultEntities(
            List<String> resultImages, ProjectTeam currentTeam) {
        return resultImages.stream()
                .map(r -> ProjectImageMapper.toResultEntity(r, currentTeam))
                .toList();
    }
}
