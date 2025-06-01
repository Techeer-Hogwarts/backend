package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamRecruitmentClosedException extends BusinessException {

    public ProjectTeamRecruitmentClosedException() {
        super(ErrorCode.PROJECT_TEAM_RECRUITMENT_CLOSED);
    }
}
