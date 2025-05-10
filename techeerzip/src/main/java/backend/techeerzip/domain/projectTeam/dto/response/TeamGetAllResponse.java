package backend.techeerzip.domain.projectTeam.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import backend.techeerzip.domain.projectTeam.type.TeamType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ProjectTeamGetAllResponse.class, name = "PROJECT"),
    @JsonSubTypes.Type(value = StudyTeamGetAllResponse.class, name = "STUDY")
})
public interface TeamGetAllResponse {

    String getName();

    LocalDateTime getCreatedAt();

    boolean isDeleted();

    boolean isRecruited();

    boolean isFinished();

    TeamType getType();
}
