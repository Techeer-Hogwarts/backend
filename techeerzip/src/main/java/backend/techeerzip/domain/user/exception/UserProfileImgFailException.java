package backend.techeerzip.domain.user.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class UserProfileImgFailException extends BusinessException {
    public UserProfileImgFailException() {
        super(ErrorCode.USER_PROFILE_IMG_FAIL);
    }
}
