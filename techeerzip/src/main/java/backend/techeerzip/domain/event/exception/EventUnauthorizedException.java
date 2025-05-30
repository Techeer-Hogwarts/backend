package backend.techeerzip.domain.event.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class EventUnauthorizedException extends BusinessException {
    public EventUnauthorizedException() {
        super(ErrorCode.EVENT_UNAUTHORIZED);
    }
}
