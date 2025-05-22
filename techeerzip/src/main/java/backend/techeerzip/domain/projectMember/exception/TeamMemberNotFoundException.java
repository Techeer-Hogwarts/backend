package backend.techeerzip.domain.projectMember.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TeamMemberNotFoundException extends BusinessException {

    public TeamMemberNotFoundException() {
        super(ErrorCode.PROJECT_MEMBER_NOT_FOUND);
    }
}
