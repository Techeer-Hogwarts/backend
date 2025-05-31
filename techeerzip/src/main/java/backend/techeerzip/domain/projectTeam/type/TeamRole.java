package backend.techeerzip.domain.projectTeam.type;

import java.util.function.Function;

import backend.techeerzip.domain.projectTeam.entity.ProjectTeam;

public enum TeamRole {
    BACKEND(ProjectTeam::getBackendNum),
    FRONTEND(ProjectTeam::getFrontendNum),
    FULLSTACK(ProjectTeam::getFullStackNum),
    DEVOPS(ProjectTeam::getDevopsNum),
    DATA_ENGINEER(ProjectTeam::getDataEngineerNum);

    private final Function<ProjectTeam, Integer> extractor;

    TeamRole(Function<ProjectTeam, Integer> extractor) {
        this.extractor = extractor;
    }

    public int getCount(ProjectTeam pt) {
        return this.extractor.apply(pt);
    }
}
