package backend.techeerzip.domain.user.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class UserUnauthorizedAdminException extends BusinessException {
    public UserUnauthorizedAdminException() {
        super(ErrorCode.USER_UNAUTHORIZED_ADMIN);
    }
}
