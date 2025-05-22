package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TeamDuplicateDeleteUpdateException extends BusinessException {

    public TeamDuplicateDeleteUpdateException() {
        super(ErrorCode.PROJECT_TEAM_DUPLICATE_DELETE_UPDATE);
    }
}
