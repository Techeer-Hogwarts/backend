package backend.techeerzip.domain.techBloggingChallenge.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "회차별 블로그 목록 조회 응답")
public class BlogChallengeListResponse {
    @Schema(description = "블로그 목록")
    private final List<BlogChallengeSummaryResponse> data;

    @Schema(description = "다음 페이지 존재 여부")
    private final boolean hasNext;

    @Schema(description = "다음 페이지 조회를 위한 커서 ID")
    private final Long nextCursor;

    public BlogChallengeListResponse(List<BlogChallengeSummaryResponse> blogs, int limit) {
        this.hasNext = blogs.size() > limit;
        this.data = hasNext ? blogs.subList(0, limit) : blogs;
        this.nextCursor = hasNext ? blogs.get(limit - 1).getBlogId() : null;
    }
}
