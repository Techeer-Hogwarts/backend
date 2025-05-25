package backend.techeerzip.domain.projectTeam.mapper;

import backend.techeerzip.domain.projectTeam.type.SortType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import backend.techeerzip.domain.projectTeam.type.PositionNumType;

public class TeamViewMapper {
    private static final int DEFAULT_LIMIT = 10;

    private TeamViewMapper() {}

    public static GetTeamsQuery mapToQuery(@NotNull GetTeamsQueryRequest request) {
        final SortType sortType = request.getSortType();

        return switch (sortType) {
            case UPDATE_AT_DESC -> mapWithDateCursor(request);
            case VIEW_COUNT_DESC -> mapWithCountCursor(request.getViewCountCursor(), sortType, request);
            case LIKE_COUNT_DESC -> mapWithCountCursor(request.getLikeCountCursor(), sortType, request);
        };
    }

    private static GetTeamsQuery mapWithDateCursor(GetTeamsQueryRequest req) {
        return GetTeamsQuery.builder()
                .globalId(req.getGlobalId())
                .dateCursor(Optional.ofNullable(req.getCreateAtCursor()).orElse(LocalDateTime.now()))
                .countCursor(null)
                .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                .teamTypes(Optional.ofNullable(req.getTeamTypes()).orElse(List.of()))
                .sortType(SortType.UPDATE_AT_DESC)
                .isRecruited(req.getIsRecruited())
                .isFinished(req.getIsFinished())
                .build();
    }

    private static GetTeamsQuery mapWithCountCursor(Integer count, SortType sortType, GetTeamsQueryRequest req) {
        return GetTeamsQuery.builder()
                .globalId(req.getGlobalId())
                .dateCursor(null)
                .countCursor(count)
                .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                .teamTypes(Optional.ofNullable(req.getTeamTypes()).orElse(List.of()))
                .sortType(sortType)
                .isRecruited(req.getIsRecruited())
                .isFinished(req.getIsFinished())
                .build();
    }
}
