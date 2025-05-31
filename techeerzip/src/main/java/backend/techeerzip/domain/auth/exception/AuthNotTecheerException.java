package backend.techeerzip.domain.auth.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class AuthNotTecheerException extends BusinessException {
    public AuthNotTecheerException() {
        super(ErrorCode.AUTH_NOT_TECHEER);
    }
}
