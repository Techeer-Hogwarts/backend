package backend.techeerzip.domain.projectTeam.mapper;

import backend.techeerzip.domain.projectTeam.dto.request.GetProjectTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ProjectQueryMapper {
    private static final int DEFAULT_LIMIT = 10;

    public static GetProjectTeamsQuery mapToQuery(GetTeamsQueryRequest req) {
        final GetProjectTeamsQuery.GetProjectTeamsQueryBuilder builder = GetProjectTeamsQuery.builder()
                .idCursor(req.getId())
                .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                .sortType(req.getSortType())
                .isRecruited(req.getIsRecruited())
                .isFinished(req.getIsFinished())
                .positionNumTypes(PositionNumType.fromMany(
                        Optional.ofNullable(req.getPositions()).orElse(List.of()))
                );

        return switch (req.getSortType()) {
            case UPDATE_AT_DESC -> builder
                    .dateCursor(Optional.ofNullable(req.getCreateAtCursor()).orElse(LocalDateTime.now()))
                    .build();
            case VIEW_COUNT_DESC -> builder
                    .countCursor(req.getViewCountCursor())
                    .build();
            case LIKE_COUNT_DESC -> builder
                    .countCursor(req.getLikeCountCursor())
                    .build();
        };
    }
}
