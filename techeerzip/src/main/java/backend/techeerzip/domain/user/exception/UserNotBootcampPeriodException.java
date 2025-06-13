package backend.techeerzip.domain.user.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class UserNotBootcampPeriodException extends BusinessException {
    public UserNotBootcampPeriodException() {
        super(ErrorCode.USER_NOT_BOOTCAMP_PERIOD);
    }
}
