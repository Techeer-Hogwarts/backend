package backend.techeerzip.domain.techBloggingChallenge.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TechBloggingRoundAlreadyExistsException extends BusinessException {
    public TechBloggingRoundAlreadyExistsException() {
        super(ErrorCode.ROUND_ALREADY_EXISTS);
    }
}
