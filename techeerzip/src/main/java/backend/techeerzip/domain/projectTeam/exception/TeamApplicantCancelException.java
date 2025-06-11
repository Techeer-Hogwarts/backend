package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TeamApplicantCancelException extends BusinessException {
    public TeamApplicantCancelException() {
        super(ErrorCode.TEAM_APPLICANT_CANCEL);
    }
}
