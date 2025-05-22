package backend.techeerzip.domain.projectTeam.type;

public enum TeamType {
    PROJECT("project"),
    STUDY("study");

    private final String type;

    TeamType(String type) {
        this.type = type;
    }
}
