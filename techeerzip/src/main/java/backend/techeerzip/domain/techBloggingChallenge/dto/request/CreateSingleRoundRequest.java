package backend.techeerzip.domain.techBloggingChallenge.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import backend.techeerzip.domain.techBloggingChallenge.validator.ValidDateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ValidDateRange
@Schema(name = "CreateSingleRoundRequest", description = "단일 회차 생성 요청 DTO")
public class CreateSingleRoundRequest {
    @NotNull(message = "회차 시작 날짜는 필수입니다")
    @Future(message = "회차 시작 날짜는 현재 날짜 이후여야 합니다")
    @Schema(description = "회차 시작 날짜", example = "2024-03-01")
    private LocalDate startDate;

    @NotNull(message = "회차 종료 날짜는 필수입니다")
    @Future(message = "회차 종료 날짜는 현재 날짜 이후여야 합니다")
    @Schema(description = "회차 종료 날짜", example = "2024-03-14")
    private LocalDate endDate;

    @Positive(message = "회차 순서는 양수여야 합니다")
    @Schema(description = "회차 순서", example = "1")
    private int sequence;

    @Schema(description = "상반기 여부 (true: 상반기, false: 하반기)", example = "true")
    private boolean isFirstHalf;
}
