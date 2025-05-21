package backend.techeerzip.domain.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "BlogQueryRequest", description = "블로그 목록 조회 요청 DTO")
public class BlogQueryRequest {
    @Schema(description = "마지막으로 조회한 블로그의 ID", example = "1")
    private Long cursorId;

    @Schema(
            description = "검색할 카테고리 (TECHEER: 테커인 블로그, SHARED: 외부 블로그)",
            example = "TECHEER",
            allowableValues = {"TECHEER", "SHARED"})
    private String category;

    @Schema(
            description = "정렬 기준 (latest: 최신순, popular: 인기순, name: 이름순)",
            example = "latest",
            allowableValues = {"latest", "popular", "name"})
    private String sortBy = "latest";

    @Schema(description = "가져올 개수", example = "10")
    private Integer limit = 10;
}
