package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamInvalidTeamRoleException extends BusinessException {
    public ProjectTeamInvalidTeamRoleException() {
        super(ErrorCode.PROJECT_TEAM_INVALID_TEAM_ROLE);
    }
}
