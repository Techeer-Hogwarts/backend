package backend.techeerzip.domain.blog.dto.request;

import jakarta.validation.constraints.Min;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "BlogBestQueryRequest", description = "인기 블로그 목록 조회 요청 DTO")
public class BlogBestQueryRequest {
    @Schema(description = "마지막으로 조회한 블로그의 ID", example = "0")
    @Min(value = 0, message = "cursorId는 0 이상의 정수여야 합니다.")
    private Long cursorId = 0L;

    @Schema(description = "가져올 개수", example = "10")
    @Min(value = 1, message = "limit은 1 이상의 정수여야 합니다.")
    private Integer limit = 10;
}
