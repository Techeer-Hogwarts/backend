package backend.techeerzip.domain.techBloggingChallenge.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "UpdateRoundRequest", description = "특정 분기의 회차 시작 날짜와 종료 날짜를 수정하는 Request")
public class UpdateRoundRequest {
    @Schema(description = "특정 분기의 회차 Id값", example = "1")
    private Long roundId;

    @Schema(description = "특정 분기의 회차 시작 날짜", example = "2025-03")
    private LocalDate startDate;

    @Schema(description = "특정 분기의 회차 종료 날짜", example = "2025-07")
    private LocalDate endDate;
}
