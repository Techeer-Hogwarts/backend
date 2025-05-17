package backend.techeerzip.domain.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetEventListQueryRequest {

    @Schema(description = "검색할 키워드")
    private String keyword;

    @Schema(description = "카테고리")
    private List<String> category;

    @Schema(description = "오프셋", example = "0")
    @Min(0)
    private int offset = 0;

    @Schema(description = "가져올 개수", example = "10")
    @Min(1)
    private int limit = 10;
}
