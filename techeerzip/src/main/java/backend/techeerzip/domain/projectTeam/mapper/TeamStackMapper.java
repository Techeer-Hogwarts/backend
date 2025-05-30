package backend.techeerzip.domain.projectTeam.mapper;

import backend.techeerzip.domain.projectTeam.dto.request.TeamStackInfo;
import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;
import backend.techeerzip.domain.projectTeam.entity.TeamStack;

public class TeamStackMapper {

    private TeamStackMapper() {}

    public static TeamStackInfo.WithName toDto(TeamStack teamStack) {
        return new TeamStackInfo.WithName(teamStack.getStack().getName(), teamStack.isMain());
    }

    public static TeamStack toEntity(TeamStackInfo.WithStack dto, ProjectTeam team) {
        return TeamStack.builder()
                .stack(dto.getStack())
                .isMain(dto.getIsMain())
                .projectTeam(team)
                .build();
    }
}
