package backend.techeerzip.domain.resume.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ResumeException extends BusinessException {
    public ResumeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
