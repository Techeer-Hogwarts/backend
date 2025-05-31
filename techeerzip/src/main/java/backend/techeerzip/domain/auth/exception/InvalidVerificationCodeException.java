package backend.techeerzip.domain.auth.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class InvalidVerificationCodeException extends BusinessException {
    public InvalidVerificationCodeException() {
        super(ErrorCode.AUTH_INVALID_EMAIL_CODE);
    }
}
