package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectImageException extends BusinessException {

    public ProjectImageException() {
        super(ErrorCode.PROJECT_TEAM_MAIN_IMAGE_BAD_REQUEST);
    }
}
