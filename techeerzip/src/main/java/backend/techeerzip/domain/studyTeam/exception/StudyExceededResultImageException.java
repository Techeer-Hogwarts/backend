package backend.techeerzip.domain.studyTeam.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class StudyExceededResultImageException extends BusinessException {
    public StudyExceededResultImageException() {
        super(ErrorCode.EXCEEDED_RESULT_IMAGE);
    }
}
