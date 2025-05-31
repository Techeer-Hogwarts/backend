package backend.techeerzip.global.exception;

public class CursorException extends BusinessException {
    public CursorException() {
        super(ErrorCode.INVALID_CURSOR_ID);
    }
}
