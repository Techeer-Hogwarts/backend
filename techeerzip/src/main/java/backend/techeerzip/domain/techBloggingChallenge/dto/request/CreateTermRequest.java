package backend.techeerzip.domain.techBloggingChallenge.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import backend.techeerzip.domain.techBloggingChallenge.validator.ValidYear;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "CreateTermRequest", description = "챌린지 기간 생성 요청 (회차 포함)")
public class CreateTermRequest {
    @Schema(description = "챌린지 연도(현재 연도 이후만 가능)", example = "2025")
    @ValidYear
    @Positive(message = "연도는 양수여야 합니다")
    @NotNull(message = "연도는 필수입니다")
    private int year;

    @Schema(description = "상반기 여부 (true: 상반기, false: 하반기)", example = "true")
    private boolean firstHalf;

    @Positive(message = "업로드 간격은 양수여야 합니다")
    @Schema(description = "챌린지 회차 업로드 간격(주)", example = "2")
    @Builder.Default
    private int intervalWeeks = 2; // 기본값 2주로 설정
}
