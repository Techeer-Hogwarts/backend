package backend.techeerzip.domain.techBloggingChallenge.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TechBloggingTermNotFoundException extends BusinessException {
    public TechBloggingTermNotFoundException() {
        super(ErrorCode.TECH_BLOGGING_TERM_NOT_FOUND);
    }
}
