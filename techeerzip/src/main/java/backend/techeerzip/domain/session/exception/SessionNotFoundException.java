package backend.techeerzip.domain.session.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SessionNotFoundException extends BusinessException {
    public SessionNotFoundException() {
        super(ErrorCode.SESSION_NOT_FOUND);
    }
}
