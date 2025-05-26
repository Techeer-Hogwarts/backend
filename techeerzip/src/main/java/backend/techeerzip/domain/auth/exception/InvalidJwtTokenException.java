package backend.techeerzip.domain.auth.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class InvalidJwtTokenException extends BusinessException {
    public InvalidJwtTokenException() {
        super(ErrorCode.AUTH_INVALID_TOKEN);
    }
}
