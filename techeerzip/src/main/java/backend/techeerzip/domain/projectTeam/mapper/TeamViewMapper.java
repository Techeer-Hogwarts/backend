package backend.techeerzip.domain.projectTeam.mapper;

import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TeamViewMapper {
    private TeamViewMapper() {

    }
    public static GetTeamsQuery mapToQuery(GetTeamsQueryRequest request) {
        return GetTeamsQuery.builder()
                .globalId(request.getGlobalId())
                .createAtCursor(Optional.ofNullable(request.getCreateAtCursor())
                        .orElse(LocalDateTime.now()))
                .limit(Optional.ofNullable(request.getLimit()).orElse(10L))
                .teamTypes(Optional.ofNullable(request.getTeamTypes()).orElse(List.of()))
                .positionNumTypes(
                        PositionNumType.fromMany(Optional.ofNullable(request.getPositions())
                                .orElse(List.of())))
                .isRecruited(request.getIsRecruited())
                .isFinished(request.getIsFinished())
                .build();
    }

}
