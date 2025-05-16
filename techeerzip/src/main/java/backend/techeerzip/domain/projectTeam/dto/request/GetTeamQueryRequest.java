package backend.techeerzip.domain.projectTeam.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;

import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import backend.techeerzip.domain.projectTeam.type.PositionType;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTeamQueryRequest {

    @Nullable private UUID globalId;
    @Builder.Default private LocalDateTime createAtCursor = LocalDateTime.now();
    @Builder.Default private Long limit = 10L;
    @Builder.Default private List<TeamType> teamTypes = List.of();
    @Nullable private List<PositionType> positions;
    @Nullable private Boolean isRecruited;
    @Nullable private Boolean isFinished;

    public GetTeamQuery toQuery() {
        return GetTeamQuery.builder()
                .globalId(globalId)
                .createAtCursor(createAtCursor)
                .limit(limit)
                .teamTypes(teamTypes)
                .positionNumTypes(PositionNumType.fromMany(positions))
                .isRecruited(isRecruited)
                .isFinished(isFinished)
                .build();
    }

    @Getter
    @Builder
    public static class GetTeamQuery {

        private final UUID globalId;
        private final LocalDateTime createAtCursor;
        private final Long limit;
        private final List<TeamType> teamTypes;
        private final List<PositionNumType> positionNumTypes;
        private final Boolean isRecruited;
        private final Boolean isFinished;
    }
}
