package backend.techeerzip.domain.auth.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ExpiredJwtTokenException extends BusinessException {
    public ExpiredJwtTokenException() {
        super(ErrorCode.AUTH_EXPIRED_TOKEN);
    }
}
