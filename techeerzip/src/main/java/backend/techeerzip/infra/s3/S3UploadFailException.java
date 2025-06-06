package backend.techeerzip.infra.s3;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class S3UploadFailException extends BusinessException {
    public S3UploadFailException() {
        super(ErrorCode.S3_UPLOAD_FAIL);
    }
}
