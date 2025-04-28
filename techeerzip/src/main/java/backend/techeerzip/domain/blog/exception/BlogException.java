package backend.techeerzip.domain.blog.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class BlogException extends BusinessException {
    public BlogException(ErrorCode errorCode) {
        super(errorCode);
    }
} 