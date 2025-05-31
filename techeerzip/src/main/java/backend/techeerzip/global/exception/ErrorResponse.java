package backend.techeerzip.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "에러 응답 객체")
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "ERROR_CODE")
    private final String code;

    @Schema(description = "에러 메시지", example = "error massage")
    private final String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }
}
