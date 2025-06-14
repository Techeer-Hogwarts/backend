package backend.techeerzip.domain.techBloggingChallenge.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.techeerzip.domain.techBloggingChallenge.validator.ValidDateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ValidDateRange
@Schema(name = "CreateSingleRoundRequest", description = "단일 회차 생성 요청 DTO")
public class CreateSingleRoundRequest {

    @NotNull(message = "챌린지 기간 ID는 필수입니다")
    @Schema(description = "챌린지 기간 ID", example = "1")
    private Long termId;

    @NotNull(message = "회차 시작 날짜는 필수입니다")
    @Future(message = "회차 시작 날짜는 현재 날짜 이후여야 합니다")
    @Schema(description = "회차 시작 날짜", example = "2024-03-01")
    private LocalDate startDate;

    @NotNull(message = "회차 종료 날짜는 필수입니다")
    @FutureOrPresent(message = "회차 종료 날짜는 현재 날짜 이상이어야 합니다")
    @Schema(description = "회차 종료 날짜", example = "2024-03-14")
    private LocalDate endDate;

    @Positive(message = "회차 순서는 양수여야 합니다")
    @Schema(description = "회차 순서", example = "1")
    private int sequence;

    @Schema(description = "상반기 여부 (true: 상반기, false: 하반기)", example = "true")
    @JsonProperty("isFirstHalf")
    private boolean firstHalf;
}
