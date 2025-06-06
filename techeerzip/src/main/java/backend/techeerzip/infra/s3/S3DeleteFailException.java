package backend.techeerzip.infra.s3;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class S3DeleteFailException extends BusinessException {

    public S3DeleteFailException() {
        super(ErrorCode.S3_DELETE_FAIL);
    }
}
