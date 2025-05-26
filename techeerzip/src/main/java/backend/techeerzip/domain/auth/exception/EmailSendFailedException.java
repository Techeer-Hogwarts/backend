package backend.techeerzip.domain.auth.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class EmailSendFailedException extends BusinessException {
    public EmailSendFailedException() {
        super(ErrorCode.AUTH_EMAIL_SEND_FAILED);
    }
}
