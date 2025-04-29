package backend.techeerzip.domain.like.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class LikeException extends BusinessException {
    public LikeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
