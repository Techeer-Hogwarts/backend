package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamMissingUpdateMemberException extends BusinessException {

    public ProjectTeamMissingUpdateMemberException() {
        super(ErrorCode.PROJECT_TEAM_MISSING_UPDATE_MEMBER);
    }
}
