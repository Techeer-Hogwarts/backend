package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamResultImageException extends BusinessException {

    public ProjectTeamResultImageException() {
        super(ErrorCode.PROJECT_TEAM_EXCEEDED_RESULT_IMAGE);
    }
}
