package backend.techeerzip.domain.auth.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class MissingJwtTokenException extends BusinessException {
  public MissingJwtTokenException() {
    super(ErrorCode.AUTH_MISSING_TOKEN);
  }
}
