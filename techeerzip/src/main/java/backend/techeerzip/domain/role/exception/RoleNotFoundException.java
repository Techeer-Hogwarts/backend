package backend.techeerzip.domain.role.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class RoleNotFoundException extends BusinessException {
    public RoleNotFoundException() {
        super(ErrorCode.ROLE_NOT_FOUND);
    }
}
