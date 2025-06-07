package backend.techeerzip.domain.techBloggingChallenge.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TechBloggingTermRequiredException extends BusinessException {
    public TechBloggingTermRequiredException() {
        super(ErrorCode.TECH_BLOGGING_TERM_REQUIRED);
    }
}
