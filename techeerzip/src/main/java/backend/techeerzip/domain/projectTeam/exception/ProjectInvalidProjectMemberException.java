package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectInvalidProjectMemberException extends BusinessException {

    public ProjectInvalidProjectMemberException() {
        super(ErrorCode.PROJECT_TEAM_INVALID_PROJECT_MEMBER);
    }
}
