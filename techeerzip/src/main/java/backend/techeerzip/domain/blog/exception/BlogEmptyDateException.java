package backend.techeerzip.domain.blog.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class BlogEmptyDateException extends BusinessException {
    public BlogEmptyDateException(String postTitle) {
        super(ErrorCode.BLOG_EMPTY_DATE, String.format("날짜가 비어있습니다. 포스트: '%s'", postTitle));
    }
}
