package backend.techeerzip.domain.projectTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TeamResultImageException extends BusinessException {

    public TeamResultImageException() {
        super(ErrorCode.EXCEEDED_RESULT_IMAGE);
    }
}
