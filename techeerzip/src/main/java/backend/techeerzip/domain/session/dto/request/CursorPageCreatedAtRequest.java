package backend.techeerzip.domain.session.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "createdAt 기반 커서 페이지네이션 요청 DTO")
public record CursorPageCreatedAtRequest(
        @Schema(description = "가져올 데이터 수 (기본값: 10)", example = "10", nullable = true)
        Integer size,

        @Schema(description = "마지막으로 조회된 데이터의 ID (커서)", example = "10", nullable = true)
        Long cursor,

        @Schema(description = "마지막으로 조회된 데이터의 생성일시", example = "2025-04-10T04:06:00", nullable = true)
        LocalDateTime createdAt
) {}
