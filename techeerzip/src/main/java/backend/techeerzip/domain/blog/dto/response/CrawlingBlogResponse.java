package backend.techeerzip.domain.blog.dto.response;

import java.util.List;

import backend.techeerzip.domain.blog.dto.request.BlogSaveRequest;
import backend.techeerzip.domain.blog.entity.BlogCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "CrawlingBlogResponse", description = "크롤링된 블로그 정보 응답 DTO")
public class CrawlingBlogResponse {

    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;

    @Schema(description = "블로그 URL", example = "https://techeer.io/blog/myblog")
    private final String blogUrl;

    @Schema(description = "크롤링된 포스트 리스트")
    private final List<BlogSaveRequest> posts;

    @Schema(description = "카테고리", example = "TECHEER")
    private final BlogCategory category;
}
