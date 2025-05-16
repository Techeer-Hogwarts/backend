package backend.techeerzip.domain.projectTeam.type;

import java.util.Arrays;
import java.util.function.Function;

import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;

public enum TeamRole {
    BACKEND("Backend", ProjectTeam::getBackendNum),
    FRONTEND("Frontend", ProjectTeam::getFrontendNum),
    FULLSTACK("FullStack", ProjectTeam::getFullStackNum),
    DEVOPS("DevOps", ProjectTeam::getDevopsNum),
    DATA_ENGINEER("DataEngineer", ProjectTeam::getDataEngineerNum);

    private final String type;
    private final Function<ProjectTeam, Integer> extractor;

    TeamRole(String type, Function<ProjectTeam, Integer> extractor) {
        this.type = type;
        this.extractor = extractor;
    }

    public static boolean isTeamRole(String type) {
        return Arrays.stream(values()).anyMatch(t -> t.type.equals(type));
    }

    public static TeamRole setType(String type) {
        return Arrays.stream(values()).filter(t -> t.type.equals(type)).findFirst().orElseThrow();
    }

    public int getCount(ProjectTeam pt) {
        return this.extractor.apply(pt);
    }
}
