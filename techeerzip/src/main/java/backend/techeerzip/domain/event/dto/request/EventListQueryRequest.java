package backend.techeerzip.domain.event.dto.request;

import java.util.List;

import jakarta.validation.constraints.Min;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventListQueryRequest {

    @Schema(description = "검색할 키워드")
    private String keyword;

    @Schema(description = "카테고리")
    private List<String> category;

    @Schema(description = "마지막으로 조회한 이벤트의 ID", example = "0")
    @Min(value = 0, message = "cursorId는 0 이상의 정수여야 합니다.")
    private Long cursorId = 0L;

    @Schema(description = "가져올 개수", example = "10")
    @Min(value = 1, message = "limit은 1 이상의 정수여야 합니다.")
    private Integer limit = 10;
}
