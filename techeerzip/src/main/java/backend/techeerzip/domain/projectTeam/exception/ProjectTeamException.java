package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamException extends BusinessException {
    public ProjectTeamException(ErrorCode errorCode) {
        super(errorCode);
    }
} 