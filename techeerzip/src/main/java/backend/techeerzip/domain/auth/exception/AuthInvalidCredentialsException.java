package backend.techeerzip.domain.auth.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class AuthInvalidCredentialsException extends BusinessException {
    public AuthInvalidCredentialsException() {
        super(ErrorCode.AUTH_INVALID_CREDENTIALS);
    }
}
