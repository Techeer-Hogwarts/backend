package backend.techeerzip.domain.projectTeam.mapper;

import java.util.List;

import backend.techeerzip.domain.projectTeam.dto.request.IndexRequest;
import backend.techeerzip.domain.projectTeam.entity.ProjectResultImage;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.studyTeam.entity.StudyResultImage;
import backend.techeerzip.domain.studyTeam.entity.StudyTeam;

public class TeamIndexMapper {

    private TeamIndexMapper() {}

    public static IndexRequest.Project toProjectRequest(ProjectTeam team) {
        final List<String> resultImages =
                team.getResultImages().stream().map(ProjectResultImage::getImageUrl).toList();
        final List<String> teamStacks =
                team.getTeamStacks().stream().map(stack -> stack.getStack().getName()).toList();

        return IndexRequest.Project.builder()
                .id(team.getId())
                .name(team.getName())
                .projectExplain(team.getProjectExplain())
                .resultImages(resultImages)
                .teamStacks(teamStacks)
                .name(team.getName())
                .build();
    }

    public static IndexRequest.Study toStudyRequest(StudyTeam team) {
        final List<String> resultImages =
                team.getStudyResultImages().stream().map(StudyResultImage::getImageUrl).toList();

        return IndexRequest.Study.builder()
                .id(team.getId())
                .name(team.getName())
                .studyExplain(team.getStudyExplain())
                .resultImages(resultImages)
                .name(team.getName())
                .build();
    }
}
