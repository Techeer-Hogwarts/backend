package backend.techeerzip.domain.resume.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResumeUserListGetRequest {
    @Schema(description = "유저 ID", example = "1")
    private Long userId;

    @Schema(description = "커서 ID(이전 페이지의 마지막 이력서 ID)", example = "10")
    private Long cursorId;

    @Schema(description = "가져올 개수", example = "10")
    @Min(value = 1, message = "limit은 1 이상의 정수여야 합니다.")
    private Integer limit;
} 