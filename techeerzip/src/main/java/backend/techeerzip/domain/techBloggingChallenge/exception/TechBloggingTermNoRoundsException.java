package backend.techeerzip.domain.techBloggingChallenge.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TechBloggingTermNoRoundsException extends BusinessException {
    public TechBloggingTermNoRoundsException() {
        super(ErrorCode.TECH_BLOGGING_TERM_NO_ROUNDS);
    }
}
