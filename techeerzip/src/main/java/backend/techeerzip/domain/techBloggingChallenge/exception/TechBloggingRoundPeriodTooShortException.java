package backend.techeerzip.domain.techBloggingChallenge.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TechBloggingRoundPeriodTooShortException extends BusinessException {
    public TechBloggingRoundPeriodTooShortException() {
        super(ErrorCode.ROUND_PERIOD_TOO_SHORT);
    }
}
