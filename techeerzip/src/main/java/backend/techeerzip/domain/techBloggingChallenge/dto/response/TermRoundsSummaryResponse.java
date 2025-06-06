package backend.techeerzip.domain.techBloggingChallenge.dto.response;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "TermRoundsSummaryResponse", description = "챌린지 기간 및 회차 요약 응답 DTO")
public class TermRoundsSummaryResponse {
    @Schema(description = "챌린지 기간 ID", example = "1")
    private final Long termId;

    @Schema(description = "챌린지 기간 이름", example = "2025년 상반기 챌린지")
    private final String termName;

    @Schema(description = "챌린지 시작일", example = "2025-03-03")
    private final LocalDate startDate;

    @Schema(description = "챌린지 종료일", example = "2025-08-03")
    private final LocalDate endDate;

    @Schema(description = "회차 요약", example = "[{ 'roundId': 1, 'name': '1회차', 'period': '2025-03-03 - 2025-03-16' }, ...]")
    private final List<RoundSummary> rounds;

    @Getter
    @Builder
    public static class RoundSummary {
        @Schema(description = "회차 ID", example = "1")
        private final Long roundId;

        @Schema(description = "회차 이름", example = "1회차")
        private final String name;

        @Schema(description = "회차 기간", example = "2025-03-03 - 2025-03-16")
        private final String period;
    }
}
