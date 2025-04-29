package backend.techeerzip.domain.session.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class SessionException extends BusinessException {
    public SessionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
