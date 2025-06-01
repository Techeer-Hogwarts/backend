package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamMainImageException extends BusinessException {

    public ProjectTeamMainImageException() {
        super(ErrorCode.PROJECT_TEAM_MAIN_IMAGE_BAD_REQUEST);
    }
}
