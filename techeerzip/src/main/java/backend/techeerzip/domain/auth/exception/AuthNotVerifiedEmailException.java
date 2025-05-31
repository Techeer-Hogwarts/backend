package backend.techeerzip.domain.auth.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class AuthNotVerifiedEmailException extends BusinessException {
    public AuthNotVerifiedEmailException() {
        super(ErrorCode.AUTH_NOT_VERIFIED_EMAIL);
    }
}
