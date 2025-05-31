package backend.techeerzip.domain.techBloggingChallenge.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TechBloggingTermAlreadyExistsException extends BusinessException {
    public TechBloggingTermAlreadyExistsException() {
        super(ErrorCode.TECH_BLOGGING_TERM_ALREADY_EXISTS);
    }
}
