package backend.techeerzip.domain.blog.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class BlogAlreadyExistsException extends BusinessException {
    public BlogAlreadyExistsException() {
        super(ErrorCode.BLOG_ALREADY_EXISTS);
    }
}