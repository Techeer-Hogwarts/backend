package backend.techeerzip.domain.projectMember.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TeamInvalidActiveRequester extends BusinessException {

    public TeamInvalidActiveRequester() {
        super(ErrorCode.TEAM_INVALID_ACTIVE_REQUESTER);
    }

    public TeamInvalidActiveRequester(String message) {
        super(ErrorCode.TEAM_INVALID_ACTIVE_REQUESTER, message);
    }
}
