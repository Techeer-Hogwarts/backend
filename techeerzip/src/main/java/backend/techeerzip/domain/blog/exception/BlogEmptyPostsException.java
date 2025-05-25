package backend.techeerzip.domain.blog.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class BlogEmptyPostsException extends BusinessException {
    public BlogEmptyPostsException() {
        super(ErrorCode.BLOG_EMPTY_POSTS);
    }
}
