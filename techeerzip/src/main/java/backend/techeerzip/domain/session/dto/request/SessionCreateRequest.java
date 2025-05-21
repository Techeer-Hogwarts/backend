package backend.techeerzip.domain.session.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SessionCreateRequest(
        @Schema(example = "https://example.com", description = "썸네일 url")
        @NotBlank(message = "썸네일 URL을 입력해주세요")
        String thumbnail,

        @Schema(example = "발표 내용", description = "세션 제목")
        @NotBlank(message = "세션 제목을 입력해주세요")
        String title,

        @Schema(example = "김테커", description = "발표자")
        @NotBlank(message = "발표자 이름을 입력해주세요")
        String presenter,

        @Schema(example = "SUMMER_2025", description = "세션 기간")
        @NotBlank(message = "세션 기간을 입력해주세요")
        String date,

        @Schema(example = "BACKEND", description = "포지션")
        @NotBlank(message = "포지션을 입력해주세요")
        String position,

        @Schema(example = "BOOTCAMP", description = "카테고리")
        @NotBlank(message = "카테고리를 입력해주세요")
        String category,

        @Schema(example = "https://example.com", description = "세션 영상 url")
        String videoUrl,

        @Schema(example = "https://example.com", description = "발표 자료 url")
        String fileUrl

) {}