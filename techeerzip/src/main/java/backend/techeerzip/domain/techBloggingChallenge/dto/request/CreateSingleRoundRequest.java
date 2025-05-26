package backend.techeerzip.domain.techBloggingChallenge.dto.request;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreateSingleRoundRequest", description = "단일 챌린지 회차 생성 요청")
public class CreateSingleRoundRequest {
    @Schema(description = "회차 시작 날짜", example = "2024-03-01")
    private LocalDate startDate;

    @Schema(description = "회차 종료 날짜", example = "2024-03-14")
    private LocalDate endDate;

    @Schema(description = "회차 순서", example = "1")
    private int sequence;

    @Schema(description = "상반기 여부 (true: 상반기, false: 하반기)", example = "true")
    private boolean isFirstHalf;
}