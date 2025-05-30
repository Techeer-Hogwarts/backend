package backend.techeerzip.domain.projectTeam.dto.request;

import java.time.LocalDateTime;

import backend.techeerzip.domain.projectTeam.type.SortType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetStudyTeamsQuery {
    private final Long idCursor;
    private final LocalDateTime dateCursor;
    private final Integer countCursor;

    private final int limit;

    private final SortType sortType;
    private final Boolean isRecruited;
    private final Boolean isFinished;
}
