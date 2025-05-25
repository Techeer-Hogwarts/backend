package backend.techeerzip.domain.projectTeam.mapper;

import backend.techeerzip.domain.projectTeam.dto.request.GetStudyTeamsQuery;
import backend.techeerzip.domain.projectTeam.dto.request.GetTeamsQueryRequest;
import java.time.LocalDateTime;
import java.util.Optional;

public class StudyQueryMapper {
    private StudyQueryMapper() {}

    private static final int DEFAULT_LIMIT = 10;

    public static GetStudyTeamsQuery mapToQuery(GetTeamsQueryRequest req) {
        GetStudyTeamsQuery.GetStudyTeamsQueryBuilder builder = GetStudyTeamsQuery.builder()
                .idCursor(req.getId())
                .limit(Optional.ofNullable(req.getLimit()).orElse(DEFAULT_LIMIT))
                .sortType(req.getSortType())
                .isRecruited(req.getIsRecruited())
                .isFinished(req.getIsFinished());

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
