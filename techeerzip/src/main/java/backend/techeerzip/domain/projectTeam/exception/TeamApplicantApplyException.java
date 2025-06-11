package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TeamApplicantApplyException extends BusinessException {
    public TeamApplicantApplyException() {
        super(ErrorCode.TEAM_APPLICANT_APPLY);
    }
}
