package backend.techeerzip.domain.techBloggingChallenge.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RoundListResponse", description = "챌린지 회차 목록 조회 응답 DTO")
public class RoundListResponse {
    @Schema(description = "챌린지 회차 목록", example = "[{roundId: 1, roundName: '2025 상반기 1회차', sequence: 1, startDate: '2025-03-01', endDate: '2025-03-07', isFirstHalf: true}, {roundId: 2, roundName: '2025 상반기 2회차', sequence: 2, startDate: '2025-03-08', endDate: '2025-03-14', isFirstHalf: true}]")
    private List<RoundDetailResponse> rounds;
}
