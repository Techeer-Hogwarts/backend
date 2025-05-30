package backend.techeerzip.domain.event.entity;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EventNotFoundException extends BusinessException {
    public EventNotFoundException() {
        super(ErrorCode.EVENT_NOT_FOUND);
    }
}
