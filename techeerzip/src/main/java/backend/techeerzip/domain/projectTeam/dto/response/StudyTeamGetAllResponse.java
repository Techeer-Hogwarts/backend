package backend.techeerzip.domain.projectTeam.dto.response;

import java.time.LocalDateTime;

import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyTeamGetAllResponse implements TeamGetAllResponse {

    private final Long id;
    private final String name;
    private final String studyExplain;
    private final boolean isDeleted;
    private final boolean isFinished;
    private final boolean isRecruited;
    private final int recruitNum;
    private final LocalDateTime createdAt;
    private final TeamType type = TeamType.STUDY;
}
