package backend.techeerzip.domain.projectTeam.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;

public class TeamViewMapper {
    private static final long DEFAULT_LIMIT = 10L;

    private TeamViewMapper() {}

    public static GetTeamsQuery mapToQuery(@NotNull GetTeamsQueryRequest request) {
        return GetTeamsQuery.builder()
                .globalId(request.getGlobalId())
                .createAtCursor(
                        Optional.ofNullable(request.getCreateAtCursor())
                                .orElse(LocalDateTime.now()))
                .limit(Optional.ofNullable(request.getLimit()).orElse(DEFAULT_LIMIT))
                .teamTypes(Optional.ofNullable(request.getTeamTypes()).orElse(List.of()))
                .positionNumTypes(
                        PositionNumType.fromMany(
                                Optional.ofNullable(request.getPositions()).orElse(List.of())))
                .isRecruited(request.getIsRecruited())
                .isFinished(request.getIsFinished())
                .build();
    }
}
