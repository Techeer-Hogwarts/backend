package backend.techeerzip.domain.techBloggingChallenge.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TermSummaryResponse", description = "챌린지 기간 요약 응답 DTO")
public class TermSummaryResponse {
    @Schema(description = "챌린지 기간 ID", example = "1")
    private Long termId;

    @Schema(description = "챌린지 기간 이름", example = "2025년 상반기 챌린지")
    private String termName;

    @Schema(description = "챌린지 시작일", example = "2025-03-03")
    private LocalDate startDate;

    @Schema(description = "챌린지 종료일", example = "2025-08-03")
    private LocalDate endDate;

    @Schema(description = "총 회차 수", example = "11")
    private int totalRounds;
}
