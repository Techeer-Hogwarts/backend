package backend.techeerzip.domain.event.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(example = "CreateEventRequest", description = "이벤트 생성 DTO")
public class EventCreateRequest {

    @Schema(example = "TECHEER", description = "카테고리")
    @NotBlank
    private String category;

    @Schema(example = "테커 파티", description = "행사 이름")
    @NotBlank
    private String title;

    @Schema(example = "2024-09-12T08:00:00Z", description = "시작 날짜")
    private LocalDateTime startDate;

    @Schema(example = "2024-09-13T08:00:00Z", description = "종료 날짜")
    private LocalDateTime endDate;

    @Schema(example = "https://example.com", description = "링크")
    private String url;
}
