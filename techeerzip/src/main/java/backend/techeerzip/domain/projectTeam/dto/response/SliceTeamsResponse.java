package backend.techeerzip.domain.projectTeam.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import backend.techeerzip.domain.projectTeam.type.TeamType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface SliceTeamsResponse {

    String getName();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    int getViewCount();

    int getLikeCount();

    boolean isDeleted();

    boolean isRecruited();

    boolean isFinished();

    TeamType getType();
}
