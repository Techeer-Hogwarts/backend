package backend.techeerzip.domain.resume.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResumeInvalidTypeException extends BusinessException {
    public ResumeInvalidTypeException() {
        super(ErrorCode.RESUME_INVALID_TYPE);
    }
}
