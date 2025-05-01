package backend.techeerzip.domain.stack.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class StackException extends BusinessException {
    public StackException(ErrorCode errorCode) {
        super(errorCode);
    }
}
