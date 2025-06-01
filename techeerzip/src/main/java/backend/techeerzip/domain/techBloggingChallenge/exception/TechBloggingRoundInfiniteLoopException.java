package backend.techeerzip.domain.techBloggingChallenge.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TechBloggingRoundInfiniteLoopException extends BusinessException {
    public TechBloggingRoundInfiniteLoopException() {
        super(ErrorCode.ROUND_INFINITE_LOOP);
    }
}
