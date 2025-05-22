package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectDuplicateTeamName extends BusinessException {

    public ProjectDuplicateTeamName() {
        super(ErrorCode.PROJECT_TEAM_DUPLICATE_TEAM_NAME);
    }

    public ProjectDuplicateTeamName(String message) {
        super(ErrorCode.PROJECT_TEAM_DUPLICATE_TEAM_NAME, message);
    }
}
