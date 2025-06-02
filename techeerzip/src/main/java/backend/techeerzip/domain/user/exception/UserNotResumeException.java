package backend.techeerzip.domain.user.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class UserNotResumeException extends BusinessException {
    public UserNotResumeException() {
        super(ErrorCode.USER_NOT_RESUME);
    }
}
