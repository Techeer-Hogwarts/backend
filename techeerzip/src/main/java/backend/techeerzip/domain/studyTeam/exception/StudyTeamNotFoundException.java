package backend.techeerzip.domain.studyTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class StudyTeamNotFoundException extends BusinessException {
    public StudyTeamNotFoundException() {
        super(ErrorCode.STUDY_TEAM_NOT_FOUND);
    }
}
