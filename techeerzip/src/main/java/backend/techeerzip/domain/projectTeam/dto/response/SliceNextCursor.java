package backend.techeerzip.domain.projectTeam.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.domain.projectTeam.type.SortType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SliceNextCursor {

    private final Boolean hasNext;
    private final Long id;
    private final LocalDateTime dateCursor;
    private final Integer countCursor;
    private final SortType sortType;
}
