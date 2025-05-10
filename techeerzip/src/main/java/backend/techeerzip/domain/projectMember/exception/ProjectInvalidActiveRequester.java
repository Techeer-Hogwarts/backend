package backend.techeerzip.domain.projectMember.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectInvalidActiveRequester extends BusinessException {

    public ProjectInvalidActiveRequester() {
        super(ErrorCode.PROJECT_MEMBER_INVALID_ACTIVE_REQUESTER);
    }

    public ProjectInvalidActiveRequester(String message) {
        super(ErrorCode.PROJECT_MEMBER_INVALID_ACTIVE_REQUESTER, message);
    }
}
