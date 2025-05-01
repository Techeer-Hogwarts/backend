package backend.techeerzip.domain.studyMember.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class StudyMemberException extends BusinessException {
    public StudyMemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
