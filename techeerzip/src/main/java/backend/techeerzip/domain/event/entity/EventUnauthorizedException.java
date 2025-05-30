package backend.techeerzip.domain.event.entity;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class EventUnauthorizedException extends BusinessException {
    public EventUnauthorizedException() {
        super(ErrorCode.EVENT_UNAUTHORIZED);
    }
}
