package backend.techeerzip.domain.studyTeam.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonTypeName;

import backend.techeerzip.domain.projectTeam.dto.response.SliceTeamsResponse;
import backend.techeerzip.domain.projectTeam.type.TeamType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonTypeName("STUDY")
public class StudySliceTeamsResponse implements SliceTeamsResponse {

    private final Long id;
    private final String name;
    private final String studyExplain;
    private final boolean isDeleted;
    private final boolean isFinished;
    private final boolean isRecruited;
    private final int recruitNum;
    private final int likeCount;
    private final int viewCount;
    private final LocalDateTime updatedAt;
    private final LocalDateTime createdAt;
    private final TeamType type = TeamType.STUDY;
}
