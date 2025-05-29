package backend.techeerzip.domain.blog.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class BlogCrawlingException extends BusinessException {
    public BlogCrawlingException(String message) {
        super(ErrorCode.BLOG_CRAWLING_ERROR, message);
    }
}
