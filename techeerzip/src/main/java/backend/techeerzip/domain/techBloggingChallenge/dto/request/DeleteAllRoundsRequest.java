package backend.techeerzip.domain.techBloggingChallenge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DeleteAllRoundsRequest", description = "특정 연도의 상/하반기 회차 전체 삭제 요청")
public class DeleteAllRoundsRequest {
    @Schema(description = "삭제할 회차의 연도", example = "2024")
    private int year;

    @Schema(description = "상반기 여부 (true: 상반기, false: 하반기)", example = "true")
    @JsonProperty("isFirstHalf")
    private boolean firstHalf;
}
