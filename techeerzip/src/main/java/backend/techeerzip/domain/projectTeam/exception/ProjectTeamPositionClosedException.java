package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamPositionClosedException extends BusinessException {

    public ProjectTeamPositionClosedException() {
        super(ErrorCode.PROJECT_TEAM_POSITION_CLOSED);
    }
}
