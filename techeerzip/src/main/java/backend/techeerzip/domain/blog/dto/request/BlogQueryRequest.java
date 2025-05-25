package backend.techeerzip.domain.blog.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "BlogQueryRequest", description = "블로그 목록 조회 요청 DTO")
public class BlogQueryRequest {
        @Schema(description = "검색할 카테고리 (TECHEER: 테커인 블로그, SHARED: 외부 블로그)", example = "TECHEER", allowableValues = {
                        "TECHEER", "SHARED" })
        @Pattern(regexp = "^(TECHEER|SHARED)$", message = "카테고리는 TECHEER 또는 SHARED만 허용됩니다.")
        private String category;

        @Schema(description = "오프셋", example = "0")
        @Min(value = 0, message = "offset은 0 이상의 정수여야 합니다.")
        private Integer offset = 0;

        @Schema(description = "가져올 개수", example = "10")
        @Min(value = 1, message = "limit은 1 이상의 정수여야 합니다.")
        private Integer limit = 10;

        public Long getCursorId() {
                return (long) offset + limit;
        }

        public String getSortBy() {
                return "latest";
        }

}
