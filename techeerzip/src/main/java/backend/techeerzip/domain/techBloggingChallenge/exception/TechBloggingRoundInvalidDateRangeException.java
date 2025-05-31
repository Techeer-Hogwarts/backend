package backend.techeerzip.domain.techBloggingChallenge.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TechBloggingRoundInvalidDateRangeException extends BusinessException {
    public TechBloggingRoundInvalidDateRangeException() {
        super(ErrorCode.ROUND_INVALID_DATE_RANGE);
    }
}
