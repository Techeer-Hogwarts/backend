package backend.techeerzip.domain.studyTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class StudyTeamInvalidRecruitNumException extends BusinessException {
    public StudyTeamInvalidRecruitNumException() {
        super(ErrorCode.STUDY_TEAM_INVALID_RECRUIT_NUM);
    }
}
