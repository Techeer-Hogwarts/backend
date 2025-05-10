package backend.techeerzip.domain.projectTeam.type;

import java.util.Arrays;

public enum TeamRole {
    BACKEND("Backend"),
    FRONTEND("Frontend"),
    FULLSTACK("FullStack"),
    DEVOPS("DevOps"),
    DATA_ENGINEER("DataEngineer"),
    INVALID("Invalid");

    private final String type;

    TeamRole(String type) {
        this.type = type;
    }

    public static boolean isTeamRole(String type) {
        return Arrays.stream(values()).anyMatch(t -> t.type.equals(type));
    }

    public static TeamRole setType(String type) {
        return Arrays.stream(values()).filter(t -> t.type.equals(type)).findFirst().orElseThrow();
    }
}
