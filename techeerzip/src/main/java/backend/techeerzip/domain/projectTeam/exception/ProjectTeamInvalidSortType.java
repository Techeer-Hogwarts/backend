package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class ProjectTeamInvalidSortType extends BusinessException {
    public ProjectTeamInvalidSortType() {
        super(ErrorCode.TEAM_INVALID_SORT_TYPE);
    }

}
