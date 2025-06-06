package backend.techeerzip.domain.resume.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ResumeUnAuthorizedException extends BusinessException {
    public ResumeUnAuthorizedException() {
        super(ErrorCode.RESUME_UNAUTHORIZED);
    }
}
