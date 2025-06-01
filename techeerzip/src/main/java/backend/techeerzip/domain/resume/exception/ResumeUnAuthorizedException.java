package backend.techeerzip.domain.resume.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResumeUnAuthorizedException extends BusinessException {
    public ResumeUnAuthorizedException() {
        super(ErrorCode.RESUME_UNAUTHORIZED);
    }
}