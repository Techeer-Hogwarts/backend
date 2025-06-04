package backend.techeerzip.domain.blog.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "블로그 목록 조회 응답")
public class BlogListResponse {
    @Schema(description = "블로그 목록")
    private final List<BlogResponse> data;

    @Schema(description = "다음 페이지 존재 여부")
    private final boolean hasNext;

    @Schema(description = "다음 페이지 조회를 위한 커서 ID")
    private final Long nextCursor;

    public BlogListResponse(List<BlogResponse> blogs, int limit) {
        this.hasNext = blogs.size() > limit;
        this.data = hasNext ? blogs.subList(0, limit) : blogs;
        this.nextCursor = hasNext ? blogs.get(limit - 1).getId() : null;
    }

    public BlogListResponse(List<BlogResponse> data, boolean hasNext, Long nextCursor) {
        this.data = data;
        this.hasNext = hasNext;
        this.nextCursor = nextCursor;
    }
}
