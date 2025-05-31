package backend.techeerzip.domain.event.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(
        description = "이벤트 생성 DTO",
        example =
                """
    {
      "category": "TECHEER",
      "title": "테커 파티",
      "startDate": "2024-09-12T08:00:00Z",
      "endDate": "2024-09-13T08:00:00Z",
      "url": "https://example.com"
    }
    """)
@Data
public class EventCreateRequest {

    @Schema(example = "TECHEER", description = "카테고리")
    @NotBlank
    @Pattern(regexp = "TECHEER|CONFERENCE|JOBINFO", message = "유효하지 않은 카테고리입니다. 유효한 값: TECHEER, CONFERENCE, JOBINFO")
    private String category;

    @Schema(example = "테커 파티", description = "행사 이름")
    @NotBlank
    private String title;

    @Schema(example = "2024-09-12T08:00:00Z", description = "시작 날짜")
    private LocalDateTime startDate;

    @Schema(example = "2024-09-13T08:00:00Z", description = "종료 날짜")
    private LocalDateTime endDate;

    @URL(message = "유효한 URL 형식이어야 합니다")
    @Schema(example = "https://example.com", description = "링크")
    private String url;
}
