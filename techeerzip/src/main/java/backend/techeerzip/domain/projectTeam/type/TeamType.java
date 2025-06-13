package backend.techeerzip.domain.projectTeam.type;

import lombok.Getter;

@Getter
public enum TeamType {
    PROJECT("project"),
    STUDY("study");

    private final String low;
    TeamType(String low) {
        this.low = low;
    }
}
