package backend.techeerzip.domain.studyTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class StudyTeamDuplicateException extends BusinessException {
    public StudyTeamDuplicateException() {
        super(ErrorCode.STUDY_TEAM_DUPLICATE_TEAM_NAME);
    }
}
