package backend.techeerzip.infra.s3;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class S3DeleteFailException extends BusinessException {

    public S3DeleteFailException() {
        super(ErrorCode.STUDY_TEAM_NOT_FOUND);
    }
}
