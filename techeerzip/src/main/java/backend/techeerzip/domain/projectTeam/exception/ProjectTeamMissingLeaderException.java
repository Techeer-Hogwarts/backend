package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamMissingLeaderException extends BusinessException {

    public ProjectTeamMissingLeaderException() {
        super(ErrorCode.PROJECT_TEAM_MISSING_LEADER);
    }
}
