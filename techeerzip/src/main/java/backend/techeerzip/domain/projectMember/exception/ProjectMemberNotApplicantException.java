package backend.techeerzip.domain.projectMember.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectMemberNotApplicantException extends BusinessException {

    public ProjectMemberNotApplicantException() {
        super(ErrorCode.PROJECT_MEMBER_NOT_APPLICANT);
    }
}
