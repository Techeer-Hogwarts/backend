package backend.techeerzip.domain.blog.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class BlogUserNotFoundException extends BusinessException {
    public BlogUserNotFoundException() {
        super(ErrorCode.BLOG_USER_NOT_FOUND);
    }
}