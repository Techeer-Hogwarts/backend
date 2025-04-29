package backend.techeerzip.domain.user.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class UserException extends BusinessException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
