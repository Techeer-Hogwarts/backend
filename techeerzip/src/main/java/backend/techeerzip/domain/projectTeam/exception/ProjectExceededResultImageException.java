package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectExceededResultImageException extends BusinessException {

    public ProjectExceededResultImageException() {
        super(ErrorCode.PROJECT_TEAM_EXCEEDED_RESULT_IMAGE);
    }
}
