package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TeamInvalidRecruitNumException extends BusinessException {
    public TeamInvalidRecruitNumException() {
        super(ErrorCode.TEAM_INVALID_RECRUIT_NUM);
    }

}
