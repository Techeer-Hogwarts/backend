package backend.techeerzip.domain.projectTeam.dto.response;

import java.util.List;

import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamUnionSliceResult {
    private final List<UnionSliceTeam> unionSliceTeams;
    private final SliceNextCursor sliceNextInfo;

    public List<Long> getIdsByType(TeamType type) {
        return unionSliceTeams.stream()
                .filter(e -> e.getTeamType() == type)
                .map(UnionSliceTeam::getId)
                .toList();
    }

    public List<Long> getProjectIds() {
        return getIdsByType(TeamType.PROJECT);
    }

    public List<Long> getStudyIds() {
        return getIdsByType(TeamType.STUDY);
    }
}
