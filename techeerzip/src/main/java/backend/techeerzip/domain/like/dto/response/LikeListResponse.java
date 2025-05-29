package backend.techeerzip.domain.like.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "좋아요 목록 조회 응답")
public class LikeListResponse {
    @Schema(description = "좋아요한 콘텐츠 목록")
    private final List<LikedContentResponse> data;

    @Schema(description = "다음 페이지 존재 여부")
    private final boolean hasNext;

    @Schema(description = "다음 페이지 조회를 위한 커서 ID")
    private final Long nextCursor;

    public LikeListResponse(List<LikedContentResponse> contents, int limit) {
        this.hasNext = contents.size() > limit;
        this.data = hasNext ? contents.subList(0, limit) : contents;
        this.nextCursor = hasNext ? contents.get(limit - 1).id() : null;
    }
}
