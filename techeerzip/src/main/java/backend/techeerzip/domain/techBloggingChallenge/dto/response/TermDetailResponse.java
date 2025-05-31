package backend.techeerzip.domain.techBloggingChallenge.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TermDetailResponse", description = "챌린지 기간 상세 조회 응답 DTO")
public class TermDetailResponse {
    @Schema(description = "챌린지 기간 ID", example = "1")
    private Long termId;

    @Schema(description = "챌린지 기간 이름", example = "2025년 상반기 챌린지")
    private String termName;

    @Schema(description = "챌린지 연도", example = "2025")
    private int year;

    @Schema(description = "챌린지 상반기 여부", example = "true")
    private boolean firstHalf;

    @Schema(description = "생성일시", example = "2025-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "회차 목록")
    private List<RoundDetailResponse> rounds;
}
