package backend.techeerzip.domain.session.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class SessionUnauthorizedException extends BusinessException {
    public SessionUnauthorizedException() {
        super(ErrorCode.SESSION_UNAUTHORIZED);
    }
}
