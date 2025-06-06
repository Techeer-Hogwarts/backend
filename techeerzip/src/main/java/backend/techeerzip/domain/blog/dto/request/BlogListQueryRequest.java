package backend.techeerzip.domain.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(name = "BlogListQueryRequest", description = "블로그 목록 조회 요청 DTO")
public record BlogListQueryRequest(
                @Schema(description = "검색할 카테고리 (TECHEER: 테커인 블로그, SHARED: 외부 블로그)", example = "TECHEER", allowableValues = {
                                "TECHEER",
                                "SHARED" }) @Pattern(regexp = "^(TECHEER|SHARED)$", message = "카테고리는 TECHEER 또는 SHARED만 허용됩니다.") @NotNull @Valid String category,

                @Schema(description = "마지막으로 조회한 블로그의 ID", example = "0") @Min(value = 0, message = "cursorId는 0 이상의 정수여야 합니다.") @Valid Long cursorId,

                @Schema(description = "가져올 개수", example = "10") @Min(value = 1, message = "limit은 1 이상의 정수여야 합니다.") @NotNull @Valid Integer limit,

                @Schema(description = "정렬 기준 (latest: 최신순, viewCount: 조회수순, name: 이름순)", example = "latest", allowableValues = {
                                "latest", "viewCount",
                                "name" }) @Pattern(regexp = "^(latest|viewCount|name)$", message = "정렬 기준은 latest, viewCount, name 중 하나여야 합니다.") @NotNull @Valid String sortBy){
        public BlogListQueryRequest {
                if (limit == null) {
                        limit = 10;
                }
                if (sortBy == null || sortBy.trim().isEmpty()) {
                        sortBy = "latest";
                }
        }
}
