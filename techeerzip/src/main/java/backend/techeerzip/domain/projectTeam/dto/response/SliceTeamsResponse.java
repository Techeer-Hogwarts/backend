package backend.techeerzip.domain.projectTeam.dto.response;

import backend.techeerzip.domain.studyTeam.dto.StudySliceTeamsResponse;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import backend.techeerzip.domain.projectTeam.type.TeamType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ProjectSliceTeamsResponse.class, name = "PROJECT"),
    @JsonSubTypes.Type(value = StudySliceTeamsResponse.class, name = "STUDY")
})
public interface SliceTeamsResponse {

    String getName();

    LocalDateTime getCreatedAt();

    boolean isDeleted();

    boolean isRecruited();

    boolean isFinished();

    TeamType getType();
}
