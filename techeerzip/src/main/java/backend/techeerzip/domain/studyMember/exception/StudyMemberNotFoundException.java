package backend.techeerzip.domain.studyMember.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class StudyMemberNotFoundException extends BusinessException {

    public StudyMemberNotFoundException() {
        super(ErrorCode.STUDY_TEAM_NOT_FOUND);
    }
}
