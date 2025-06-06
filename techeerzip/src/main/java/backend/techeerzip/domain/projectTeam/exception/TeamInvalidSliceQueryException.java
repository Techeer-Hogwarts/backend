package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TeamInvalidSliceQueryException extends BusinessException {
    public TeamInvalidSliceQueryException() {
        super(ErrorCode.TEAM_INVALID_SLICE_QUERY);
    }
}
