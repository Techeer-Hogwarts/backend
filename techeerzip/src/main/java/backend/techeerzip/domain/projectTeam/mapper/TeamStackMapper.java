package backend.techeerzip.domain.projectTeam.mapper;

import backend.techeerzip.domain.projectTeam.dto.request.TeamStackInfo;
import backend.techeerzip.domain.projectTeam.entity.TeamStack;

public class TeamStackMapper {

    private TeamStackMapper() {}

    public static TeamStackInfo.WithName toDto(TeamStack teamStack) {
        return new TeamStackInfo.WithName(teamStack.getStack().getName(), teamStack.isMain());
    }

    // public static TeamStack toEntity(TeamStackInfo.WithName dto, Stack stack, ProjectTeam team) {
    //     return TeamStack.builder()
    //             .stack(stack)
    //             .projectTeam(team)
    //             .isMain(dto.getIsMain())
    //             .build();
    // }
}
