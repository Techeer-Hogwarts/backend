package backend.techeerzip.infra.redis.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class RedisMessageProcessingException extends BusinessException {
    public RedisMessageProcessingException() {
        super(ErrorCode.REDIS_MESSAGE_PROCESSING_ERROR);
    }

    public RedisMessageProcessingException(String message) {
        super(ErrorCode.REDIS_MESSAGE_PROCESSING_ERROR, message);
    }
}