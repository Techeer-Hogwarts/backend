package backend.techeerzip.domain.session.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class SessionUnauthorizedException extends BusinessException {
    public SessionUnauthorizedException() {
        super(ErrorCode.SESSION_UNAUTHORIZED);
    }
}
