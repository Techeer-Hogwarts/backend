package backend.techeerzip.domain.like.dto.response.author;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "블로그 작성자 응답")
public record BlogAuthorResponse(
        @Schema(description = "작성자 이름") String authorName,
        @Schema(description = "작성자 이미지 URL") String authorImage) {}
