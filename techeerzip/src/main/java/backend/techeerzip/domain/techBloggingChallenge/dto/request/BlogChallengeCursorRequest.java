package backend.techeerzip.domain.techBloggingChallenge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogChallengeCursorRequest {
    @Schema(description = "챌린지 기간(분기) ID", example = "1")
    private Long termId;

    @Schema(description = "회차 ID", example = "1")
    private Long roundId;

    @Schema(description = "커서 ID (마지막으로 조회한 블로그의 ID)", example = "1")
    private Long cursorId;

    @Schema(description = "조회할 개수", example = "10")
    private int limit = 10;

    @Schema(description = "정렬 옵션(latest, viewCount, name)", example = "latest")
    private String sortBy = "latest";
}
