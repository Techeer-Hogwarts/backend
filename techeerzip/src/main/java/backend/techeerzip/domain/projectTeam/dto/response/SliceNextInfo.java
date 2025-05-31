package backend.techeerzip.domain.projectTeam.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import backend.techeerzip.domain.projectTeam.type.SortType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SliceNextInfo {

    private final Boolean hasNext;
    private final UUID globalId;
    private final Long id;
    private final LocalDateTime dateCursor;
    private final Integer countCursor;
    private final SortType sortType;
}
