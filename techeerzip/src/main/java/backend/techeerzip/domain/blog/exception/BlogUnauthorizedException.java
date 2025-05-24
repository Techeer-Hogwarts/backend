package backend.techeerzip.domain.blog.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class BlogUnauthorizedException extends BusinessException {
    public BlogUnauthorizedException() {
        super(ErrorCode.BLOG_UNAUTHORIZED);
    }
}