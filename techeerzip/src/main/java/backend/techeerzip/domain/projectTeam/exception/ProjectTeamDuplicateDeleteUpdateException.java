package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamDuplicateDeleteUpdateException extends BusinessException {

    public ProjectTeamDuplicateDeleteUpdateException() {
        super(ErrorCode.PROJECT_TEAM_DUPLICATE_DELETE_UPDATE);
    }
}
