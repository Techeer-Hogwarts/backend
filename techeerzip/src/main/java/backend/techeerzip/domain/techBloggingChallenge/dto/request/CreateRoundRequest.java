package backend.techeerzip.domain.techBloggingChallenge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CreateRoundRequest", description = "챌린지 회차 생성 요청")
public class CreateRoundRequest {
    @Schema(description = "챌린지 연도", example = "2025")
    private int year;

    @Schema(description = "상반기 여부 (true: 상반기, false: 하반기)", example = "true")
    private boolean isFirstHalf;

    @Schema(description = "챌린지 회차 업로드 간격(주)", example = "2")
    private int intervalWeeks;
}
