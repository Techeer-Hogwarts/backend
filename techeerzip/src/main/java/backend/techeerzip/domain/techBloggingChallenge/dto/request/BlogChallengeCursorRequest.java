package backend.techeerzip.domain.techBloggingChallenge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "BlogChallengeCursorRequest", description = "회차별 블로그 커서 기반 조회 요청 DTO")
@Builder
public class BlogChallengeCursorRequest {

    @Schema(description = "챌린지 기간(분기) ID", example = "1")
    private Long termId;

    @Schema(description = "정렬 옵션(latest, viewCount, name)", example = "latest")
    private String sort = "latest";

    @Schema(description = "커서(마지막 blogId)", example = "100")
    private Long cursorId;

    @Schema(description = "가져올 개수", example = "10")
    private Integer limit = 10;

    @Schema(description = "회차 ID", example = "1")
    private Long roundId;

    public Integer getLimit() {
        return limit;
    }

    public Long getRoundId() {
        return roundId;
    }

    public Long getCursorId() {
        return cursorId;
    }
}
