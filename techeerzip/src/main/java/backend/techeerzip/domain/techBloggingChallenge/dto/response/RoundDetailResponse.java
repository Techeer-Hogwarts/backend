package backend.techeerzip.domain.techBloggingChallenge.dto.response;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RoundDetailResponse", description = "챌린지 회차 상세 조회 응답 DTO")
public class RoundDetailResponse {
    @Schema(description = "챌린지 회차 Id값", example = "1")
    private Long roundId;

    @Schema(description = "챌린지 회차 이름", example = "2025 상반기 1회차")
    private String roundName;

    @Schema(description = "챌린지 회차 순서", example = "1")
    private int sequence;

    @Schema(description = "챌린지 회차 시작 날짜", example = "2025-03-01")
    private LocalDate startDate;

    @Schema(description = "챌린지 회차 종료 날짜", example = "2025-03-07")
    private LocalDate endDate;

    @Schema(description = "챌린지 회차 상반기 여부", example = "true")
    private boolean isFirstHalf;
}
