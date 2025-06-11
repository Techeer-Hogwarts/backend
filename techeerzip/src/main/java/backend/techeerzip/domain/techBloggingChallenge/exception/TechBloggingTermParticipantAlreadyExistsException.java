package backend.techeerzip.domain.techBloggingChallenge.exception;

import backend.techeerzip.global.exception.BusinessException;
import backend.techeerzip.global.exception.ErrorCode;

public class TechBloggingTermParticipantAlreadyExistsException extends BusinessException {
    public TechBloggingTermParticipantAlreadyExistsException() {
        super(ErrorCode.TECH_BLOGGING_TERM_ALREADY_JOINED);
    }
}
