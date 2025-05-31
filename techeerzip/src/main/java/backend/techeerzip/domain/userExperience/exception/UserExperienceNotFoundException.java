package backend.techeerzip.domain.userExperience.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class UserExperienceNotFoundException extends BusinessException {
    public UserExperienceNotFoundException() {
        super(ErrorCode.USER_EXPERIENCE_NOT_FOUND);
    }
}
