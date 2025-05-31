package backend.techeerzip.domain.techBloggingChallenge.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TechBloggingRoundNotFoundException extends BusinessException {
    public TechBloggingRoundNotFoundException() {
        super(ErrorCode.ROUND_NOT_FOUND);
    }
}
