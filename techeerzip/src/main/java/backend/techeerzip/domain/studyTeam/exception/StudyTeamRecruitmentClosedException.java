package backend.techeerzip.domain.studyTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class StudyTeamRecruitmentClosedException extends BusinessException {
    public StudyTeamRecruitmentClosedException() {
        super(ErrorCode.STUDY_TEAM_CLOSED_RECRUIT);
    }
}
