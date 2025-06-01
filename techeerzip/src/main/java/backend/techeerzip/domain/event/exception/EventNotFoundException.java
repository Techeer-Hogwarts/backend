package backend.techeerzip.domain.event.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EventNotFoundException extends BusinessException {
    public EventNotFoundException() {
        super(ErrorCode.EVENT_NOT_FOUND);
    }
}
