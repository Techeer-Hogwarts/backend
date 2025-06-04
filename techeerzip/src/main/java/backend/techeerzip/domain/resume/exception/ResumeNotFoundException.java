package backend.techeerzip.domain.resume.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResumeNotFoundException extends BusinessException {
    public ResumeNotFoundException() {
        super(ErrorCode.RESUME_NOT_FOUND);
    }
}
