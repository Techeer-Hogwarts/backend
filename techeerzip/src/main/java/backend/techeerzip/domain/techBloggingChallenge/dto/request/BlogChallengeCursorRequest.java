package backend.techeerzip.domain.techBloggingChallenge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record BlogChallengeCursorRequest(
        @Schema(description = "챌린지 기간(분기) ID", example = "1") Long termId,

        @Schema(description = "회차 ID", example = "1") Long roundId,

        @Schema(description = "커서 ID (마지막으로 조회한 블로그의 ID)", example = "1") Long cursorId,

        @Schema(description = "조회할 개수", example = "10") @Min(value = 1, message = "limit은 1 이상이어야 합니다.") int limit,

        @Schema(description = "정렬 옵션(latest, viewCount, name)", example = "latest") @Pattern(regexp = "latest|viewCount|name", message = "sortBy는 latest, viewCount, name 중 하나여야 합니다.") String sortBy) {
}
