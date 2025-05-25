package backend.techeerzip.domain.blog.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class BlogInvalidDateFormatException extends BusinessException {
    public BlogInvalidDateFormatException(String postTitle, String dateStr) {
        super(
                ErrorCode.BLOG_INVALID_DATE_FORMAT,
                String.format("잘못된 날짜 형식입니다. 포스트: '%s', 날짜: '%s'", postTitle, dateStr));
    }
}
