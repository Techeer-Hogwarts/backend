package backend.techeerzip.domain.studyMember.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class StudyMemberBadRequestException extends BusinessException {
    public StudyMemberBadRequestException() {
        super(ErrorCode.STUDY_MEMBER_BAD_REQUEST);
    }
}
