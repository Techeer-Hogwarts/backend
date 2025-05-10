package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamNotFoundException extends BusinessException {

    public ProjectTeamNotFoundException() {
        super(ErrorCode.PROJECT_TEAM_NOT_FOUND);
    }
}
