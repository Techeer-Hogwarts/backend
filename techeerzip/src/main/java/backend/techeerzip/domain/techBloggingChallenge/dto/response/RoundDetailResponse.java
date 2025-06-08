package backend.techeerzip.domain.techBloggingChallenge.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "RoundDetailResponse", description = "챌린지 회차 상세 조회 응답 DTO")
public class RoundDetailResponse {
    @Schema(description = "챌린지 회차 Id값", example = "1")
    private final Long roundId;

    @Schema(description = "챌린지 기간 ID", example = "1")
    private final Long termId;

    @Schema(description = "챌린지 회차 이름", example = "2025 상반기 1회차")
    private final String roundName;

    @Schema(description = "챌린지 기간 이름", example = "2025년 상반기 챌린지")
    private final String termName;

    @Schema(description = "챌린지 회차 순서", example = "1")
    private final int sequence;

    @Schema(description = "챌린지 회차 시작 날짜", example = "2025-03-01")
    private final LocalDate startDate;

    @Schema(description = "챌린지 회차 종료 날짜", example = "2025-03-07")
    private final LocalDate endDate;

    @Schema(description = "챌린지 연도", example = "2025")
    private final int year;

    @Schema(description = "챌린지 상반기 여부", example = "true")
    private final boolean firstHalf;
}
