package backend.techeerzip.domain.projectTeam.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnionSliceTeam {

    private Long id;
    private TeamType teamType;
    private LocalDateTime updatedAt;
    private Integer viewCount;
    private Integer likeCount;
}
