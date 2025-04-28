package backend.techeerzip.domain.event.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class EventException extends BusinessException {
    public EventException(ErrorCode errorCode) {
        super(errorCode);
    }
} 