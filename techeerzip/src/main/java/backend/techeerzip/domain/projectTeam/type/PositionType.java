package backend.techeerzip.domain.projectTeam.type;

public enum PositionType {
    FRONTEND("frontend"),
    BACKEND("backend"),
    DEVOPS("devops"),
    FULLSTACK("fullstack"),
    DATA_ENGINEER("dataEngineer");

    private final String type;

    PositionType(String type) {
        this.type = type;
    }
}
