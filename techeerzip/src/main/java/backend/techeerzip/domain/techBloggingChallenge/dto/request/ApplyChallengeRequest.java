package backend.techeerzip.domain.techBloggingChallenge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ApplyChallengeRequest", description = "특정 분기 챌린지 지원 요청 DTO")
public class ApplyChallengeRequest {

    @Schema(description = "챌린지 연도", example = "2025")
    private int year;

    @Schema(description = "상반기 여부 (true: 상반기, false: 하반기)", example = "true")
    private boolean firstHalf;
}
