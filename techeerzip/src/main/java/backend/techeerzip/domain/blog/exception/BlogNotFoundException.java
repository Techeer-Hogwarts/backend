package backend.techeerzip.domain.blog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BlogNotFoundException extends BusinessException {
    public BlogNotFoundException() {
        super(ErrorCode.BLOG_NOT_FOUND);
    }
}
