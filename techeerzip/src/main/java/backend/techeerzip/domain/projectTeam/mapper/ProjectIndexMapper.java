package backend.techeerzip.domain.projectTeam.mapper;

import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.request.ProjectIndexRequest;
import backend.techeerzip.domain.projectTeam.entity.ProjectResultImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;

public class ProjectIndexMapper {

    private ProjectIndexMapper() {}

    public static ProjectIndexRequest toIndexRequest(ProjectTeam team) {
        final List<String> resultImages =
                team.getResultImages().stream().map(ProjectResultImage::getImageUrl).toList();
        final List<String> teamStacks =
                team.getTeamStacks().stream().map(stack -> stack.getStack().getName()).toList();

        return new ProjectIndexRequest(
                team.getId(),
                team.getName(),
                team.getProjectExplain(),
                resultImages,
                teamStacks,
                team.getName() // title = name
                );
    }
}
