package backend.techeerzip.domain.projectTeam.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamUnionInfo {

    private UUID globalId;
    private Long id;
    private TeamType teamType;
    private LocalDateTime dateCursor;
    private Integer countCursor;
}
