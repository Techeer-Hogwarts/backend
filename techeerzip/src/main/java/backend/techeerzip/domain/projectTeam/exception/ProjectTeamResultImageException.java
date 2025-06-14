package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamResultImageException extends BusinessException {

    public ProjectTeamResultImageException() {
        super(ErrorCode.TEAM_RESULT_IMAGE_BAD_REQUEST);
    }
}
