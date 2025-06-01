package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TeamInvalidSliceQueryException extends BusinessException {
    public TeamInvalidSliceQueryException() {
        super(ErrorCode.PROJECT_TEAM_INVALID_TEAM_ROLE);
    }

}
