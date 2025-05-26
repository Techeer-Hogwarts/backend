package backend.techeerzip.domain.user.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
