package backend.techeerzip.domain.projectMember.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectMemberException extends BusinessException {
    public ProjectMemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
