package backend.techeerzip.domain.projectTeam.type;

import lombok.Getter;

@Getter
public enum TeamType {
    PROJECT("project"),
    STUDY("study");

    private final String type;

    TeamType(String type) {
        this.type = type;
    }
}
