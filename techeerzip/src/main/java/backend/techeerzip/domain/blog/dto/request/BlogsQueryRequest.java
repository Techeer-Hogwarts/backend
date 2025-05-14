package backend.techeerzip.domain.blog.dto.request;


import backend.techeerzip.domain.blog.entity.BlogCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(name = "BlogsQueryRequest", description = "블로그 목록 조회 요청 DTO")
public class BlogsQueryRequest {
    @Schema(
            description = "검색할 카테고리 (TECHEER: 테커인 블로그, SHARED: 외부 블로그)",
            example = "TECHEER",
            allowableValues = {"TECHEER", "SHARED"}
    )
    private String category;

    @Schema(description = "오프셋", example = "0")
    @Min(value = 0, message = "offset은 0 이상의 정수여야 합니다.")
    private Integer offset = 0;

    @Schema(description = "가져올 개수", example = "10")
    @Min(value = 1, message = "limit은 1 이상의 정수여야 합니다.")
    private Integer limit = 10;
}