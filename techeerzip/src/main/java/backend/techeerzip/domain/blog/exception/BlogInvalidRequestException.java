package backend.techeerzip.domain.blog.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class BlogInvalidRequestException extends BusinessException {
    public BlogInvalidRequestException(String message) {
        super(ErrorCode.BLOG_INVALID_REQUEST, message);
    }

    public BlogInvalidRequestException() {
        super(ErrorCode.BLOG_INVALID_REQUEST);
    }
}