package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectInvalidTeamStackException extends BusinessException {

    public ProjectInvalidTeamStackException() {
        super(ErrorCode.PROJECT_TEAM_INVALID_TEAM_STACK);
    }
}
