package backend.techeerzip.domain.techBloggingChallenge.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TechBloggingRoundPastDateException extends BusinessException {
    public TechBloggingRoundPastDateException() {
        super(ErrorCode.ROUND_PAST_DATE);
    }
}
